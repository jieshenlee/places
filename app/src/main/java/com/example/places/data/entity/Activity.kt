package com.example.places.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "activities")
data class Activity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val title: String,
    val location: String,
    val notes: String = "",
    val date: Date,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isCompleted: Boolean = false,
    val category: ActivityCategory = ActivityCategory.GENERAL,
    val imageUrls: List<String> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null
)

enum class ActivityCategory {
    GENERAL,
    SIGHTSEEING,
    FOOD,
    ACCOMMODATION,
    TRANSPORTATION,
    SHOPPING,
    ENTERTAINMENT,
    OUTDOOR,
    CULTURAL
}
