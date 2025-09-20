package com.example.places.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.places.data.entity.Activity
import com.example.places.data.entity.ActivityCategory
import java.util.*

@Dao
interface ActivityDao {
    
    @Query("SELECT * FROM activities WHERE userId = :userId ORDER BY date ASC")
    fun getActivitiesByUser(userId: String): LiveData<List<Activity>>
    
    @Query("SELECT * FROM activities WHERE userId = :userId AND date >= :startDate AND date <= :endDate ORDER BY date ASC")
    fun getActivitiesByDateRange(userId: String, startDate: Date, endDate: Date): LiveData<List<Activity>>
    
    @Query("SELECT * FROM activities WHERE userId = :userId AND DATE(date) = DATE(:date) ORDER BY date ASC")
    fun getActivitiesByDate(userId: String, date: Date): LiveData<List<Activity>>
    
    @Query("SELECT * FROM activities WHERE id = :id")
    suspend fun getActivityById(id: String): Activity?
    
    @Query("SELECT * FROM activities WHERE userId = :userId AND isCompleted = 0 ORDER BY date ASC")
    fun getPendingActivities(userId: String): LiveData<List<Activity>>
    
    @Query("SELECT * FROM activities WHERE userId = :userId AND category = :category ORDER BY date ASC")
    fun getActivitiesByCategory(userId: String, category: ActivityCategory): LiveData<List<Activity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: Activity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<Activity>)
    
    @Update
    suspend fun updateActivity(activity: Activity)
    
    @Query("UPDATE activities SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateActivityCompletion(id: String, isCompleted: Boolean)
    
    @Delete
    suspend fun deleteActivity(activity: Activity)
    
    @Query("DELETE FROM activities WHERE userId = :userId")
    suspend fun deleteAllUserActivities(userId: String)
    
    @Query("DELETE FROM activities WHERE date < :cutoffDate")
    suspend fun deleteOldActivities(cutoffDate: Date)
}
