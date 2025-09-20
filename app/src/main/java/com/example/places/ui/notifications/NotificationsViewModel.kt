package com.example.places.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.places.data.entity.Notification
import com.example.places.data.repository.NotificationRepository
import com.example.places.data.repository.UserRepository
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _todayNotifications = MutableLiveData<List<Notification>>()
    val todayNotifications: LiveData<List<Notification>> = _todayNotifications

    private val _yesterdayNotifications = MutableLiveData<List<Notification>>()
    val yesterdayNotifications: LiveData<List<Notification>> = _yesterdayNotifications

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _hasNotifications = MutableLiveData<Boolean>()
    val hasNotifications: LiveData<Boolean> = _hasNotifications

    fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = userRepository.getCurrentUser()
                currentUser?.let { user ->
                    // Observe today's notifications
                    notificationRepository.getTodayNotifications(user.id).observeForever { notifications ->
                        _todayNotifications.value = notifications
                        updateHasNotifications()
                    }
                    
                    // Observe yesterday's notifications
                    notificationRepository.getYesterdayNotifications(user.id).observeForever { notifications ->
                        _yesterdayNotifications.value = notifications
                        updateHasNotifications()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun updateHasNotifications() {
        val todayCount = _todayNotifications.value?.size ?: 0
        val yesterdayCount = _yesterdayNotifications.value?.size ?: 0
        _hasNotifications.value = (todayCount + yesterdayCount) > 0
    }

    fun markNotificationAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                notificationRepository.markAsRead(notificationId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getCurrentUser()
                currentUser?.let { user ->
                    notificationRepository.markAllAsRead(user.id)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun handleNotificationClick(notification: Notification) {
        // Mark as read when clicked
        markNotificationAsRead(notification.id)
        
        // TODO: Navigate based on notification type
        when (notification.type) {
            com.example.places.data.entity.NotificationType.LIKE,
            com.example.places.data.entity.NotificationType.COMMENT -> {
                // Navigate to travel card detail
                notification.relatedEntityId?.let { travelCardId ->
                    // TODO: Navigate to TravelCardDetailActivity
                }
            }
            com.example.places.data.entity.NotificationType.FOLLOW -> {
                // Navigate to user profile
                // TODO: Navigate to user profile
            }
            else -> {
                // Handle other notification types
            }
        }
    }
}

class NotificationsViewModelFactory(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationsViewModel(notificationRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
