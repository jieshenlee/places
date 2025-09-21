package com.example.places.data.repository

import androidx.lifecycle.LiveData
import com.example.places.data.dao.PublishedActivityDao
import com.example.places.data.entity.PublishedActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class PublishedActivityRepository(private val publishedActivityDao: PublishedActivityDao) {
    
    fun getAllPublishedActivities(): LiveData<List<PublishedActivity>> {
        return publishedActivityDao.getAllPublishedActivities()
    }
    
    fun getPublishedActivitiesByUser(username: String): LiveData<List<PublishedActivity>> {
        return publishedActivityDao.getPublishedActivitiesByUser(username)
    }
    
    suspend fun getPublishedActivityById(id: String): PublishedActivity? {
        return withContext(Dispatchers.IO) {
            publishedActivityDao.getPublishedActivityById(id)
        }
    }
    
    suspend fun insertPublishedActivity(publishedActivity: PublishedActivity) {
        withContext(Dispatchers.IO) {
            publishedActivityDao.insertPublishedActivity(publishedActivity)
        }
    }
    
    suspend fun updatePublishedActivity(publishedActivity: PublishedActivity) {
        withContext(Dispatchers.IO) {
            val updatedActivity = publishedActivity.copy(updatedAt = Date())
            publishedActivityDao.updatePublishedActivity(updatedActivity)
        }
    }
    
    suspend fun deletePublishedActivity(publishedActivity: PublishedActivity) {
        withContext(Dispatchers.IO) {
            publishedActivityDao.deletePublishedActivity(publishedActivity)
        }
    }
    
    suspend fun deletePublishedActivityById(id: String) {
        withContext(Dispatchers.IO) {
            publishedActivityDao.deletePublishedActivityById(id)
        }
    }
    
    suspend fun toggleLike(id: String, currentLikeCount: Int, isCurrentlyLiked: Boolean) {
        withContext(Dispatchers.IO) {
            val newLikeCount = if (isCurrentlyLiked) currentLikeCount - 1 else currentLikeCount + 1
            val newLikedStatus = !isCurrentlyLiked
            publishedActivityDao.updateLike(id, newLikeCount, newLikedStatus)
        }
    }
    
    suspend fun toggleBookmark(id: String, isCurrentlyBookmarked: Boolean) {
        withContext(Dispatchers.IO) {
            publishedActivityDao.updateBookmark(id, !isCurrentlyBookmarked)
        }
    }
    
    suspend fun incrementCommentCount(id: String) {
        withContext(Dispatchers.IO) {
            val activity = publishedActivityDao.getPublishedActivityById(id)
            activity?.let {
                publishedActivityDao.updateCommentCount(id, it.commentCount + 1)
            }
        }
    }
    
    suspend fun incrementShareCount(id: String) {
        withContext(Dispatchers.IO) {
            val activity = publishedActivityDao.getPublishedActivityById(id)
            activity?.let {
                publishedActivityDao.updateShareCount(id, it.shareCount + 1)
            }
        }
    }
    
    suspend fun insertSampleData() {
        withContext(Dispatchers.IO) {
            val sampleActivities = listOf(
                PublishedActivity(
                    id = "sample_1",
                    username = "Sophia Carter",
                    userProfileImage = null,
                    location = "Galle- Sri Lanka",
                    date = "1st November 2025",
                    description = "Lorem ipsum dolor sit amet consectetur. Tellus ultrices velit sed faucibus.",
                    activityTitle = "Galle Fort",
                    activityDescription = "Lorem ipsum dolor sit amet consectetur. Tellus ultrices velit sed feugiat.",
                    activityTime = "11am- 1pm",
                    heroImage = null,
                    activityImage = null,
                    likeCount = 45,
                    commentCount = 23,
                    shareCount = 3,
                    isLiked = false,
                    isBookmarked = false,
                    createdAt = Date(System.currentTimeMillis() - 86400000), // 1 day ago
                    updatedAt = Date(System.currentTimeMillis() - 86400000)
                ),
                PublishedActivity(
                    id = "sample_2",
                    username = "John Doe",
                    userProfileImage = null,
                    location = "Colombo- Sri Lanka",
                    date = "2nd November 2025",
                    description = "Amazing experience exploring the city center and local markets.",
                    activityTitle = "Colombo City Center",
                    activityDescription = "Visited the bustling markets and historic buildings in the heart of Colombo.",
                    activityTime = "9am- 12pm",
                    heroImage = null,
                    activityImage = null,
                    likeCount = 32,
                    commentCount = 15,
                    shareCount = 7,
                    isLiked = false,
                    isBookmarked = false,
                    createdAt = Date(System.currentTimeMillis() - 43200000), // 12 hours ago
                    updatedAt = Date(System.currentTimeMillis() - 43200000)
                )
            )
            
            // Only insert if no activities exist
            val existingActivities = publishedActivityDao.getAllPublishedActivities()
            publishedActivityDao.insertAllPublishedActivities(sampleActivities)
        }
    }
    
    suspend fun deleteAllPublishedActivities() {
        withContext(Dispatchers.IO) {
            publishedActivityDao.deleteAllPublishedActivities()
        }
    }
}
