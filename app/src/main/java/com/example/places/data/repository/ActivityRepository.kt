package com.example.places.data.repository

import androidx.lifecycle.LiveData
import com.example.places.data.dao.ActivityDao
import com.example.places.data.entity.Activity
import com.example.places.data.entity.ActivityCategory
import java.util.*

class ActivityRepository(
    private val activityDao: ActivityDao
) {
    
    fun getActivitiesByUser(userId: String): LiveData<List<Activity>> = 
        activityDao.getActivitiesByUser(userId)
    
    fun getActivitiesByDateRange(userId: String, startDate: Date, endDate: Date): LiveData<List<Activity>> = 
        activityDao.getActivitiesByDateRange(userId, startDate, endDate)
    
    fun getActivitiesByDate(userId: String, date: Date): LiveData<List<Activity>> = 
        activityDao.getActivitiesByDate(userId, date)
    
    fun getPendingActivities(userId: String): LiveData<List<Activity>> = 
        activityDao.getPendingActivities(userId)
    
    fun getActivitiesByCategory(userId: String, category: ActivityCategory): LiveData<List<Activity>> = 
        activityDao.getActivitiesByCategory(userId, category)
    
    suspend fun getActivityById(id: String): Activity? = 
        activityDao.getActivityById(id)
    
    suspend fun insertActivity(activity: Activity) {
        activityDao.insertActivity(activity)
    }
    
    suspend fun updateActivity(activity: Activity) {
        activityDao.updateActivity(activity)
    }
    
    suspend fun updateActivityCompletion(id: String, isCompleted: Boolean) {
        activityDao.updateActivityCompletion(id, isCompleted)
    }
    
    suspend fun deleteActivity(activity: Activity) {
        activityDao.deleteActivity(activity)
    }
    
    suspend fun deleteAllUserActivities(userId: String) {
        activityDao.deleteAllUserActivities(userId)
    }
    
    suspend fun deleteOldActivities(cutoffDate: Date) {
        activityDao.deleteOldActivities(cutoffDate)
    }
}
