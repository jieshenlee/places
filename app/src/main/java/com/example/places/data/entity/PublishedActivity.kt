package com.example.places.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "published_activities")
data class PublishedActivity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val userProfileImage: String?,
    val location: String,
    val date: String,
    val description: String,
    val activityTitle: String,
    val activityDescription: String,
    val activityTime: String,
    val heroImage: String?,
    val activityImage: String?,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val shareCount: Int = 0,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
