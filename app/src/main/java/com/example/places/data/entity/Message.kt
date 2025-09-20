package com.example.places.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val participantIds: List<String>, // List of user IDs in the conversation
    val lastMessage: String = "",
    val lastMessageTime: Date = Date(),
    val lastMessageSenderId: String = "",
    val isRead: Boolean = true,
    val createdAt: Date = Date()
)

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val conversationId: String,
    val senderId: String,
    val content: String,
    val messageType: MessageType = MessageType.TEXT,
    val timestamp: Date = Date(),
    val isRead: Boolean = false,
    val imageUrl: String? = null,
    val travelCardId: String? = null // For sharing travel cards
)

enum class MessageType {
    TEXT,
    IMAGE,
    TRAVEL_CARD_SHARE,
    LOCATION
}
