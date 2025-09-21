package com.example.places.ui.addactivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.places.data.entity.Activity
import com.example.places.data.entity.ActivityCategory
import com.example.places.data.entity.PublishedActivity
import com.example.places.data.repository.ActivityRepository
import com.example.places.data.repository.PublishedActivityRepository
import com.example.places.data.repository.UserRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddActivityViewModel(
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository,
    private val publishedActivityRepository: PublishedActivityRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _activitySaved = MutableLiveData<Boolean>()
    val activitySaved: LiveData<Boolean> = _activitySaved

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _selectedDate = MutableLiveData<Date>()
    val selectedDate: LiveData<Date> = _selectedDate

    init {
        _selectedDate.value = Date() // Default to today
    }

    fun setSelectedDate(date: Date) {
        _selectedDate.value = date
    }

    fun saveActivity(
        location: String,
        notes: String,
        date: Date,
        category: ActivityCategory = ActivityCategory.GENERAL,
        imageUrls: List<String> = emptyList(),
        latitude: Double? = null,
        longitude: Double? = null
    ) {
        if (location.isBlank()) {
            _errorMessage.value = "Location is required"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val currentUser = userRepository.getCurrentUser()
                if (currentUser == null) {
                    _errorMessage.value = "User not found. Please log in again."
                    return@launch
                }

                // Create the regular Activity object
                val activity = Activity(
                    userId = currentUser.id,
                    title = location, // Using location as title for now
                    location = location,
                    notes = notes,
                    date = date,
                    category = category,
                    imageUrls = imageUrls,
                    latitude = latitude,
                    longitude = longitude
                )

                // Save the regular activity
                activityRepository.insertActivity(activity)

                // Create and save the PublishedActivity for the feed/profile
                val dateFormatter = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
                val publishedActivity = PublishedActivity(
                    username = currentUser.displayName ?: currentUser.email.substringBefore("@"),
                    userProfileImage = currentUser.profileImageUrl,
                    location = location,
                    date = dateFormatter.format(date),
                    description = notes.ifEmpty { "Exploring $location" },
                    activityTitle = location,
                    activityDescription = notes.ifEmpty { "A wonderful experience at $location" },
                    activityTime = "All day", // Default time, could be made configurable
                    heroImage = imageUrls.firstOrNull(),
                    activityImage = imageUrls.getOrNull(1),
                    likeCount = 0,
                    commentCount = 0,
                    shareCount = 0,
                    isLiked = false,
                    isBookmarked = false,
                    createdAt = Date(),
                    updatedAt = Date()
                )

                // Save the published activity
                publishedActivityRepository.insertPublishedActivity(publishedActivity)
                
                _activitySaved.value = true
                
            } catch (e: Exception) {
                _errorMessage.value = "Failed to save activity: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun resetSavedState() {
        _activitySaved.value = false
    }
}

class AddActivityViewModelFactory(
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository,
    private val publishedActivityRepository: PublishedActivityRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddActivityViewModel(activityRepository, userRepository, publishedActivityRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
