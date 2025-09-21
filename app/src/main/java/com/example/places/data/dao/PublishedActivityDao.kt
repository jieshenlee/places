package com.example.places.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.places.data.entity.PublishedActivity

@Dao
interface PublishedActivityDao {
    
    @Query("SELECT * FROM published_activities ORDER BY createdAt DESC")
    fun getAllPublishedActivities(): LiveData<List<PublishedActivity>>
    
    @Query("SELECT * FROM published_activities WHERE id = :id")
    suspend fun getPublishedActivityById(id: String): PublishedActivity?
    
    @Query("SELECT * FROM published_activities WHERE username = :username ORDER BY createdAt DESC")
    fun getPublishedActivitiesByUser(username: String): LiveData<List<PublishedActivity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPublishedActivity(publishedActivity: PublishedActivity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPublishedActivities(publishedActivities: List<PublishedActivity>)
    
    @Update
    suspend fun updatePublishedActivity(publishedActivity: PublishedActivity)
    
    @Delete
    suspend fun deletePublishedActivity(publishedActivity: PublishedActivity)
    
    @Query("DELETE FROM published_activities WHERE id = :id")
    suspend fun deletePublishedActivityById(id: String)
    
    @Query("UPDATE published_activities SET likeCount = :likeCount, isLiked = :isLiked WHERE id = :id")
    suspend fun updateLike(id: String, likeCount: Int, isLiked: Boolean)
    
    @Query("UPDATE published_activities SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: String, isBookmarked: Boolean)
    
    @Query("UPDATE published_activities SET commentCount = :commentCount WHERE id = :id")
    suspend fun updateCommentCount(id: String, commentCount: Int)
    
    @Query("UPDATE published_activities SET shareCount = :shareCount WHERE id = :id")
    suspend fun updateShareCount(id: String, shareCount: Int)
    
    @Query("DELETE FROM published_activities")
    suspend fun deleteAllPublishedActivities()
}
