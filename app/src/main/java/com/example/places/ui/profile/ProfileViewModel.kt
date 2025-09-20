package com.example.places.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.places.data.entity.TravelCard
import com.example.places.data.entity.User
import com.example.places.data.repository.TravelCardRepository
import com.example.places.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val travelCardRepository: TravelCardRepository
) : ViewModel() {

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _userTravelCards = MutableLiveData<List<TravelCard>>()
    val userTravelCards: LiveData<List<TravelCard>> = _userTravelCards

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = userRepository.getCurrentUser()
                _currentUser.value = user
                user?.let { loadUserTravelCards(it.id) }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadUserTravelCards(userId: String) {
        travelCardRepository.getTravelCardsByUser(userId).observeForever { cards ->
            _userTravelCards.value = cards
        }
    }

    fun refreshProfile() {
        loadCurrentUser()
    }

    fun updateUserStats(user: User) {
        viewModelScope.launch {
            try {
                val updatedUser = user.copy(
                    travelCardsCount = _userTravelCards.value?.size ?: 0
                )
                userRepository.updateUser(updatedUser)
                _currentUser.value = updatedUser
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

class ProfileViewModelFactory(
    private val userRepository: UserRepository,
    private val travelCardRepository: TravelCardRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(userRepository, travelCardRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
