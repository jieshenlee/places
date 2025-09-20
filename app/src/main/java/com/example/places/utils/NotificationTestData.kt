package com.example.places.utils

import com.example.places.data.entity.Notification
import com.example.places.data.entity.NotificationType
import java.util.*

object NotificationTestData {
    
    fun createSampleNotifications(currentUserId: String): List<Notification> {
        val now = Date()
        val calendar = Calendar.getInstance()
        
        return listOf(
            // Today's notifications
            Notification(
                userId = currentUserId,
                fromUserId = "user_sophia",
                type = NotificationType.LIKE,
                title = "New Like",
                message = "Sophia Carter liked your post",
                relatedEntityId = "travel_card_1",
                relatedEntityType = "travel_card",
                createdAt = Date(now.time - (2 * 60 * 60 * 1000)), // 2 hours ago
                imageUrl = null
            ),
            Notification(
                userId = currentUserId,
                fromUserId = "user_liam",
                type = NotificationType.COMMENT,
                title = "New Comment",
                message = "Liam Bennett commented on your post",
                relatedEntityId = "travel_card_1",
                relatedEntityType = "travel_card",
                createdAt = Date(now.time - (1 * 60 * 60 * 1000)), // 1 hour ago
                imageUrl = null
            ),
            Notification(
                userId = currentUserId,
                fromUserId = "user_isabella",
                type = NotificationType.FOLLOW,
                title = "New Follower",
                message = "Isabella Reyes started following you",
                createdAt = Date(now.time - (30 * 60 * 1000)), // 30 minutes ago
                imageUrl = null
            ),
            
            // Yesterday's notifications
            Notification(
                userId = currentUserId,
                fromUserId = "user_ethan",
                type = NotificationType.LIKE,
                title = "New Like",
                message = "Ethan Walker liked your post",
                relatedEntityId = "travel_card_2",
                relatedEntityType = "travel_card",
                createdAt = Date(now.time - (26 * 60 * 60 * 1000)), // 26 hours ago (yesterday)
                imageUrl = null
            ),
            Notification(
                userId = currentUserId,
                fromUserId = "user_olivia",
                type = NotificationType.COMMENT,
                title = "New Comment",
                message = "Olivia Harper commented on your post",
                relatedEntityId = "travel_card_2",
                relatedEntityType = "travel_card",
                createdAt = Date(now.time - (25 * 60 * 60 * 1000)), // 25 hours ago (yesterday)
                imageUrl = null
            ),
            Notification(
                userId = currentUserId,
                fromUserId = "user_noah",
                type = NotificationType.FOLLOW,
                title = "New Follower",
                message = "Noah Clark started following you",
                createdAt = Date(now.time - (24 * 60 * 60 * 1000)), // 24 hours ago (yesterday)
                imageUrl = null
            )
        )
    }
    
    suspend fun populateTestNotifications(
        notificationRepository: com.example.places.data.repository.NotificationRepository,
        currentUserId: String
    ) {
        val notifications = createSampleNotifications(currentUserId)
        notifications.forEach { notification ->
            notificationRepository.insertNotification(notification)
        }
    }
}
