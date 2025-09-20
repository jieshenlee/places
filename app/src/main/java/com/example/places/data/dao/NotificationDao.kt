package com.example.places.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.places.data.entity.Notification
import com.example.places.data.entity.NotificationType

@Dao
interface NotificationDao {
    
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotificationsByUser(userId: String): LiveData<List<Notification>>
    
    @Query("SELECT * FROM notifications WHERE userId = :userId AND DATE(createdAt) = DATE('now') ORDER BY createdAt DESC")
    fun getTodayNotifications(userId: String): LiveData<List<Notification>>
    
    @Query("SELECT * FROM notifications WHERE userId = :userId AND DATE(createdAt) = DATE('now', '-1 day') ORDER BY createdAt DESC")
    fun getYesterdayNotifications(userId: String): LiveData<List<Notification>>
    
    @Query("SELECT * FROM notifications WHERE id = :id")
    suspend fun getNotificationById(id: String): Notification?
    
    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    fun getUnreadNotificationCount(userId: String): LiveData<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<Notification>)
    
    @Update
    suspend fun updateNotification(notification: Notification)
    
    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)
    
    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: String)
    
    @Delete
    suspend fun deleteNotification(notification: Notification)
    
    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun deleteAllUserNotifications(userId: String)
    
    @Query("DELETE FROM notifications WHERE createdAt < datetime('now', '-30 days')")
    suspend fun deleteOldNotifications()
}
