package com.example.places.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey val id: String,
    val travelCardId: String,
    val userId: String,
    val userDisplayName: String,
    val userProfileImage: String?,
    val content: String,
    val createdAt: Date,
    val updatedAt: Date,
    val likesCount: Int = 0,
    val isEdited: Boolean = false
)
