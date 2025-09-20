package com.example.places.data.repository

import androidx.lifecycle.LiveData
import com.example.places.data.dao.FeedPostDao
import com.example.places.data.entity.FeedPost
import com.example.places.data.entity.FeedPostSample
import java.util.*

class FeedPostRepository(private val feedPostDao: FeedPostDao) {
    
    fun getAllPosts(): LiveData<List<FeedPost>> = feedPostDao.getAllPosts()
    
    fun getPostsByUser(userId: String): LiveData<List<FeedPost>> = 
        feedPostDao.getPostsByUser(userId)
    
    suspend fun getPostById(postId: String): FeedPost? = feedPostDao.getPostById(postId)
    
    suspend fun insertPost(post: FeedPost) = feedPostDao.insertPost(post)
    
    suspend fun updatePost(post: FeedPost) = feedPostDao.updatePost(post)
    
    suspend fun deletePost(post: FeedPost) = feedPostDao.deletePost(post)
    
    suspend fun toggleLike(postId: String, currentLikeStatus: Boolean, currentLikeCount: Int) {
        val newLikeStatus = !currentLikeStatus
        val newLikeCount = if (newLikeStatus) currentLikeCount + 1 else currentLikeCount - 1
        feedPostDao.updateLikeStatus(postId, newLikeStatus, newLikeCount)
    }
    
    suspend fun toggleBookmark(postId: String, currentBookmarkStatus: Boolean) {
        feedPostDao.updateBookmarkStatus(postId, !currentBookmarkStatus)
    }
    
    suspend fun updateCommentCount(postId: String, commentCount: Int) {
        feedPostDao.updateCommentCount(postId, commentCount)
    }
    
    fun getBookmarkedPosts(): LiveData<List<FeedPost>> = feedPostDao.getBookmarkedPosts()
    
    // Sample data for development
    suspend fun insertSampleData() {
        val samplePosts = listOf(
            FeedPost(
                id = "post_1",
                userId = "user_1",
                username = "Sherwin Jieshen Li",
                userProfileImage = null,
                location = "Galle - Sri Lanka",
                description = "Lorem ipsum dolor sit amet consectetur. Tellus ultrices velit sed et volutpat.",
                timeRange = "1:00pm to 2:00pm",
                imageUrl = null,
                likeCount = 45,
                commentCount = 23,
                shareCount = 5,
                createdAt = Date()
            ),
            FeedPost(
                id = "post_2",
                userId = "user_2",
                username = "Ethan Ramirez",
                userProfileImage = null,
                location = "Galle - Sri Lanka",
                description = "Lorem ipsum dolor sit amet consectetur. Tellus ultrices velit sed et volutpat.",
                timeRange = "1:00pm to 2:00pm",
                imageUrl = null,
                likeCount = 45,
                commentCount = 23,
                shareCount = 5,
                createdAt = Date(System.currentTimeMillis() - 3600000) // 1 hour ago
            ),
            FeedPost(
                id = "post_3",
                userId = "user_3",
                username = "Priya Kapoor",
                userProfileImage = null,
                location = "Galle - Sri Lanka",
                description = "Lorem ipsum dolor sit amet consectetur. Tellus ultrices velit sed et volutpat.",
                timeRange = "1:00pm to 2:00pm",
                imageUrl = null,
                likeCount = 45,
                commentCount = 23,
                shareCount = 5,
                createdAt = Date(System.currentTimeMillis() - 7200000) // 2 hours ago
            ),
            FeedPost(
                id = "post_4",
                userId = "user_4",
                username = "Amina Hassan",
                userProfileImage = null,
                location = "Galle - Sri Lanka",
                description = "Lorem ipsum dolor sit amet consectetur. Tellus ultrices velit sed et volutpat.",
                timeRange = "1:00pm to 2:00pm",
                imageUrl = null,
                likeCount = 45,
                commentCount = 23,
                shareCount = 5,
                createdAt = Date(System.currentTimeMillis() - 10800000) // 3 hours ago
            )
        )
        
        feedPostDao.insertPosts(samplePosts)
    }
}
