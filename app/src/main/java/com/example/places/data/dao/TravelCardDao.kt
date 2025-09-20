package com.example.places.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.places.data.entity.TravelCard

@Dao
interface TravelCardDao {
    
    @Query("SELECT * FROM travel_cards ORDER BY createdAt DESC")
    fun getAllTravelCards(): LiveData<List<TravelCard>>
    
    @Query("SELECT * FROM travel_cards WHERE userId = :userId ORDER BY createdAt DESC")
    fun getTravelCardsByUser(userId: String): LiveData<List<TravelCard>>
    
    @Query("SELECT * FROM travel_cards WHERE id = :id")
    suspend fun getTravelCardById(id: String): TravelCard?
    
    @Query("SELECT * FROM travel_cards WHERE isPublic = 1 ORDER BY createdAt DESC")
    fun getPublicTravelCards(): LiveData<List<TravelCard>>
    
    @Query("SELECT * FROM travel_cards WHERE isSynced = 0")
    suspend fun getUnsyncedTravelCards(): List<TravelCard>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTravelCard(travelCard: TravelCard)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTravelCards(travelCards: List<TravelCard>)
    
    @Update
    suspend fun updateTravelCard(travelCard: TravelCard)
    
    @Delete
    suspend fun deleteTravelCard(travelCard: TravelCard)
    
    @Query("DELETE FROM travel_cards WHERE id = :id")
    suspend fun deleteTravelCardById(id: String)
    
    @Query("UPDATE travel_cards SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}
