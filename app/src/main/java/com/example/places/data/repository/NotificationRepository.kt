package com.example.places.data.repository

import androidx.lifecycle.LiveData
import com.example.places.data.dao.NotificationDao
import com.example.places.data.entity.Notification
import com.example.places.data.entity.NotificationType
import java.util.*

class NotificationRepository(
    private val notificationDao: NotificationDao
) {
    
    fun getNotificationsByUser(userId: String): LiveData<List<Notification>> = 
        notificationDao.getNotificationsByUser(userId)
    
    fun getTodayNotifications(userId: String): LiveData<List<Notification>> = 
        notificationDao.getTodayNotifications(userId)
    
    fun getYesterdayNotifications(userId: String): LiveData<List<Notification>> = 
        notificationDao.getYesterdayNotifications(userId)
    
    fun getUnreadNotificationCount(userId: String): LiveData<Int> = 
        notificationDao.getUnreadNotificationCount(userId)
    
    suspend fun getNotificationById(id: String): Notification? = 
        notificationDao.getNotificationById(id)
    
    suspend fun insertNotification(notification: Notification) {
        notificationDao.insertNotification(notification)
    }
    
    suspend fun markAsRead(id: String) {
        notificationDao.markAsRead(id)
    }
    
    suspend fun markAllAsRead(userId: String) {
        notificationDao.markAllAsRead(userId)
    }
    
    suspend fun deleteNotification(notification: Notification) {
        notificationDao.deleteNotification(notification)
    }
    
    suspend fun deleteAllUserNotifications(userId: String) {
        notificationDao.deleteAllUserNotifications(userId)
    }
    
    // Helper methods to create notifications
    suspend fun createLikeNotification(
        userId: String,
        fromUserId: String,
        fromUserName: String,
        travelCardId: String,
        fromUserImageUrl: String? = null
    ) {
        val notification = Notification(
            userId = userId,
            fromUserId = fromUserId,
            type = NotificationType.LIKE,
            title = "New Like",
            message = "$fromUserName liked your post",
            relatedEntityId = travelCardId,
            relatedEntityType = "travel_card",
            imageUrl = fromUserImageUrl
        )
        insertNotification(notification)
    }
    
    suspend fun createCommentNotification(
        userId: String,
        fromUserId: String,
        fromUserName: String,
        travelCardId: String,
        fromUserImageUrl: String? = null
    ) {
        val notification = Notification(
            userId = userId,
            fromUserId = fromUserId,
            type = NotificationType.COMMENT,
            title = "New Comment",
            message = "$fromUserName commented on your post",
            relatedEntityId = travelCardId,
            relatedEntityType = "travel_card",
            imageUrl = fromUserImageUrl
        )
        insertNotification(notification)
    }
    
    suspend fun createFollowNotification(
        userId: String,
        fromUserId: String,
        fromUserName: String,
        fromUserImageUrl: String? = null
    ) {
        val notification = Notification(
            userId = userId,
            fromUserId = fromUserId,
            type = NotificationType.FOLLOW,
            title = "New Follower",
            message = "$fromUserName started following you",
            imageUrl = fromUserImageUrl
        )
        insertNotification(notification)
    }
}
