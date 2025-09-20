package com.example.places.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "travel_cards")
data class TravelCard(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val location: String,
    val latitude: Double?,
    val longitude: Double?,
    val imageUrls: List<String>,
    val tags: List<String>,
    val createdAt: Date,
    val updatedAt: Date,
    val isPublic: Boolean = true,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isSynced: Boolean = false
)
