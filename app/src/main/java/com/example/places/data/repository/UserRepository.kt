package com.example.places.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.example.places.data.dao.UserDao
import com.example.places.data.entity.User
import java.util.*

class UserRepository(
    private val userDao: UserDao,
    private val context: Context
) {
    
    private val sharedPrefs: SharedPreferences = 
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    
    // Room Database Operations
    suspend fun getUserById(id: String): User? = userDao.getUserById(id)
    
    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
    
    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }
    
    suspend fun updateUser(user: User) {
        val updatedUser = user.copy(updatedAt = Date())
        userDao.updateUser(updatedUser)
    }
    
    fun getAllUsers(): LiveData<List<User>> = userDao.getAllUsers()
    
    // Local User Management with SharedPreferences
    fun getCurrentUserId(): String? = sharedPrefs.getString("current_user_id", null)
    
    fun getCurrentUserEmail(): String? = sharedPrefs.getString("current_user_email", null)
    
    fun isUserLoggedIn(): Boolean = getCurrentUserId() != null
    
    suspend fun loginUser(email: String, password: String): User? {
        // Simple local authentication - in a real app, you'd hash passwords
        val user = getUserByEmail(email)
        return if (user != null) {
            // Save current user info
            sharedPrefs.edit()
                .putString("current_user_id", user.id)
                .putString("current_user_email", user.email)
                .apply()
            user
        } else {
            null
        }
    }
    
    suspend fun registerUser(email: String, password: String, displayName: String): User? {
        // Check if user already exists
        if (getUserByEmail(email) != null) {
            return null // User already exists
        }
        
        val userId = UUID.randomUUID().toString()
        val user = User(
            id = userId,
            email = email,
            displayName = displayName,
            profileImageUrl = null,
            bio = null,
            createdAt = Date(),
            updatedAt = Date()
        )
        
        insertUser(user)
        
        // Auto-login after registration
        sharedPrefs.edit()
            .putString("current_user_id", user.id)
            .putString("current_user_email", user.email)
            .apply()
        
        return user
    }
    
    fun logoutUser() {
        sharedPrefs.edit()
            .remove("current_user_id")
            .remove("current_user_email")
            .apply()
    }
    
    suspend fun getCurrentUser(): User? {
        val userId = getCurrentUserId() ?: return null
        return getUserById(userId)
    }
}
