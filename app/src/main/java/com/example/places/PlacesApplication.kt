package com.example.places

import android.app.Application
import com.example.places.data.database.PlacesDatabase
import com.example.places.data.repository.ActivityRepository
import com.example.places.data.repository.CommentRepository
import com.example.places.data.repository.FeedPostRepository
import com.example.places.data.repository.NotificationRepository
import com.example.places.data.repository.PublishedActivityRepository
import com.example.places.data.repository.TravelCardRepository
import com.example.places.data.repository.UserRepository

class PlacesApplication : Application() {
    
    // Database instance
    val database by lazy { PlacesDatabase.getDatabase(this) }
    
    // Repository instances
    val travelCardRepository: TravelCardRepository by lazy {
        TravelCardRepository(database.travelCardDao())
    }
    
    val userRepository: UserRepository by lazy {
        UserRepository(database.userDao(), this)
    }
    
    val commentRepository: CommentRepository by lazy {
        CommentRepository(database.commentDao())
    }
    
    val notificationRepository: NotificationRepository by lazy {
        NotificationRepository(database.notificationDao())
    }
    
    val activityRepository: ActivityRepository by lazy {
        ActivityRepository(database.activityDao())
    }
    
    val feedPostRepository: FeedPostRepository by lazy {
        FeedPostRepository(database.feedPostDao())
    }
    
    val publishedActivityRepository: PublishedActivityRepository by lazy {
        PublishedActivityRepository(database.publishedActivityDao())
    }
    
    override fun onCreate() {
        super.onCreate()
    }
}
