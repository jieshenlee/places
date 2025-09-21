package com.example.places.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.places.data.converter.Converters
import com.example.places.data.dao.ActivityDao
import com.example.places.data.dao.CommentDao
import com.example.places.data.dao.FeedPostDao
import com.example.places.data.dao.NotificationDao
import com.example.places.data.dao.PublishedActivityDao
import com.example.places.data.dao.TravelCardDao
import com.example.places.data.dao.UserDao
import com.example.places.data.entity.Activity
import com.example.places.data.entity.Comment
import com.example.places.data.entity.FeedPost
import com.example.places.data.entity.Notification
import com.example.places.data.entity.PublishedActivity
import com.example.places.data.entity.TravelCard
import com.example.places.data.entity.User

@Database(
    entities = [TravelCard::class, User::class, Comment::class, Notification::class, Activity::class, FeedPost::class, PublishedActivity::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PlacesDatabase : RoomDatabase() {
    
    abstract fun travelCardDao(): TravelCardDao
    abstract fun userDao(): UserDao
    abstract fun commentDao(): CommentDao
    abstract fun notificationDao(): NotificationDao
    abstract fun activityDao(): ActivityDao
    abstract fun feedPostDao(): FeedPostDao
    abstract fun publishedActivityDao(): PublishedActivityDao
    
    companion object {
        @Volatile
        private var INSTANCE: PlacesDatabase? = null
        
        fun getDatabase(context: Context): PlacesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlacesDatabase::class.java,
                    "places_database"
                ).fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
