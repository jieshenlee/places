package com.example.places.data.repository

import androidx.lifecycle.LiveData
import com.example.places.data.dao.CommentDao
import com.example.places.data.entity.Comment
import java.util.*

class CommentRepository(
    private val commentDao: CommentDao
) {
    
    // Room Database Operations
    fun getCommentsByTravelCard(travelCardId: String): LiveData<List<Comment>> = 
        commentDao.getCommentsByTravelCard(travelCardId)
    
    suspend fun getCommentById(id: String): Comment? = commentDao.getCommentById(id)
    
    suspend fun insertComment(comment: Comment) {
        commentDao.insertComment(comment)
    }
    
    suspend fun updateComment(comment: Comment) {
        val updatedComment = comment.copy(
            updatedAt = Date(),
            isEdited = true
        )
        commentDao.updateComment(updatedComment)
    }
    
    suspend fun deleteComment(comment: Comment) {
        commentDao.deleteComment(comment)
    }
    
    suspend fun deleteCommentById(id: String) {
        commentDao.deleteCommentById(id)
    }
    
    suspend fun deleteCommentsByTravelCard(travelCardId: String) {
        commentDao.deleteCommentsByTravelCard(travelCardId)
    }
}
