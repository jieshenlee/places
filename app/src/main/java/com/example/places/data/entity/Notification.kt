package com.example.places.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val userId: String, // User who will receive the notification
    val fromUserId: String, // User who triggered the notification
    val type: NotificationType,
    val title: String,
    val message: String,
    val relatedEntityId: String? = null, // Travel card ID, comment ID, etc.
    val relatedEntityType: String? = null, // "travel_card", "comment", etc.
    val isRead: Boolean = false,
    val createdAt: Date = Date(),
    val imageUrl: String? = null // Profile image of the user who triggered the notification
)

enum class NotificationType {
    LIKE,
    COMMENT,
    FOLLOW,
    MENTION,
    TRAVEL_CARD_SHARED
}
