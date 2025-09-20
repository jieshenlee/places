package com.example.places.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "feed_posts")
data class FeedPost(
    @PrimaryKey
    val id: String,
    val userId: String,
    val username: String,
    val userProfileImage: String?,
    val location: String,
    val description: String,
    val timeRange: String,
    val imageUrl: String?,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val shareCount: Int = 0,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val createdAt: Date = Date()
)

// Sample data class for feed posts
data class FeedPostSample(
    val username: String,
    val location: String,
    val date: String,
    val description: String,
    val timeRange: String,
    val imageRes: Int? = null,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val shareCount: Int = 0
)
