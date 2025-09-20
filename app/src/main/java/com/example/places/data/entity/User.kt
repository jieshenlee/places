package com.example.places.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val email: String,
    val displayName: String,
    val profileImageUrl: String?,
    val bio: String?,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val travelCardsCount: Int = 0,
    val createdAt: Date,
    val updatedAt: Date,
    val isVerified: Boolean = false
)
