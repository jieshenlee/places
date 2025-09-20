package com.example.places.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.places.data.entity.FeedPost

@Dao
interface FeedPostDao {
    
    @Query("SELECT * FROM feed_posts ORDER BY createdAt DESC")
    fun getAllPosts(): LiveData<List<FeedPost>>
    
    @Query("SELECT * FROM feed_posts WHERE userId = :userId ORDER BY createdAt DESC")
    fun getPostsByUser(userId: String): LiveData<List<FeedPost>>
    
    @Query("SELECT * FROM feed_posts WHERE id = :postId")
    suspend fun getPostById(postId: String): FeedPost?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: FeedPost)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<FeedPost>)
    
    @Update
    suspend fun updatePost(post: FeedPost)
    
    @Delete
    suspend fun deletePost(post: FeedPost)
    
    @Query("UPDATE feed_posts SET isLiked = :isLiked, likeCount = :likeCount WHERE id = :postId")
    suspend fun updateLikeStatus(postId: String, isLiked: Boolean, likeCount: Int)
    
    @Query("UPDATE feed_posts SET isBookmarked = :isBookmarked WHERE id = :postId")
    suspend fun updateBookmarkStatus(postId: String, isBookmarked: Boolean)
    
    @Query("UPDATE feed_posts SET commentCount = :commentCount WHERE id = :postId")
    suspend fun updateCommentCount(postId: String, commentCount: Int)
    
    @Query("SELECT * FROM feed_posts WHERE isBookmarked = 1 ORDER BY createdAt DESC")
    fun getBookmarkedPosts(): LiveData<List<FeedPost>>
    
    @Query("DELETE FROM feed_posts")
    suspend fun deleteAllPosts()
}
