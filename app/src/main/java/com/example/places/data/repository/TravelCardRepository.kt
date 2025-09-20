package com.example.places.data.repository

import androidx.lifecycle.LiveData
import com.example.places.data.dao.TravelCardDao
import com.example.places.data.entity.TravelCard
import java.util.*

class TravelCardRepository(
    private val travelCardDao: TravelCardDao
) {
    
    // Room Database Operations
    fun getAllTravelCards(): LiveData<List<TravelCard>> = travelCardDao.getAllTravelCards()
    
    fun getTravelCardsByUser(userId: String): LiveData<List<TravelCard>> = 
        travelCardDao.getTravelCardsByUser(userId)
    
    fun getPublicTravelCards(): LiveData<List<TravelCard>> = 
        travelCardDao.getPublicTravelCards()
    
    suspend fun getTravelCardById(id: String): TravelCard? = 
        travelCardDao.getTravelCardById(id)
    
    suspend fun insertTravelCard(travelCard: TravelCard) {
        val cardToInsert = travelCard.copy(isSynced = true) // Always synced in local-only mode
        travelCardDao.insertTravelCard(cardToInsert)
    }
    
    suspend fun updateTravelCard(travelCard: TravelCard) {
        val updatedCard = travelCard.copy(
            updatedAt = Date(),
            isSynced = true
        )
        travelCardDao.updateTravelCard(updatedCard)
    }
    
    suspend fun deleteTravelCard(travelCard: TravelCard) {
        travelCardDao.deleteTravelCard(travelCard)
    }
    
    suspend fun deleteTravelCardById(id: String) {
        travelCardDao.deleteTravelCardById(id)
    }
}
