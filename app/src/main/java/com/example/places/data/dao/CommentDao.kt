package com.example.places.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.places.data.entity.Comment

@Dao
interface CommentDao {
    
    @Query("SELECT * FROM comments WHERE travelCardId = :travelCardId ORDER BY createdAt ASC")
    fun getCommentsByTravelCard(travelCardId: String): LiveData<List<Comment>>
    
    @Query("SELECT * FROM comments WHERE id = :id")
    suspend fun getCommentById(id: String): Comment?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<Comment>)
    
    @Update
    suspend fun updateComment(comment: Comment)
    
    @Delete
    suspend fun deleteComment(comment: Comment)
    
    @Query("DELETE FROM comments WHERE id = :id")
    suspend fun deleteCommentById(id: String)
    
    @Query("DELETE FROM comments WHERE travelCardId = :travelCardId")
    suspend fun deleteCommentsByTravelCard(travelCardId: String)
}
