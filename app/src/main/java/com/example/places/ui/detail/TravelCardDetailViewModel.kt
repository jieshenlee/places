package com.example.places.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.places.data.entity.TravelCard
import com.example.places.data.entity.User
import com.example.places.data.repository.TravelCardRepository
import com.example.places.data.repository.UserRepository
import kotlinx.coroutines.launch

class TravelCardDetailViewModel(
    private val travelCardRepository: TravelCardRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _travelCard = MutableLiveData<TravelCard?>()
    val travelCard: LiveData<TravelCard?> = _travelCard

    private val _cardOwner = MutableLiveData<User?>()
    val cardOwner: LiveData<User?> = _cardOwner

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLiked = MutableLiveData<Boolean>()
    val isLiked: LiveData<Boolean> = _isLiked

    private val _isSaved = MutableLiveData<Boolean>()
    val isSaved: LiveData<Boolean> = _isSaved

    fun loadTravelCard(travelCardId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val card = travelCardRepository.getTravelCardById(travelCardId)
                _travelCard.value = card
                
                card?.let { 
                    loadCardOwner(it.userId)
                    // TODO: Load like and save status from user preferences
                    _isLiked.value = false
                    _isSaved.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadCardOwner(userId: String) {
        try {
            val user = userRepository.getUserById(userId)
            _cardOwner.value = user
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleLike() {
        val currentCard = _travelCard.value ?: return
        val isCurrentlyLiked = _isLiked.value ?: false
        
        viewModelScope.launch {
            try {
                val newLikesCount = if (isCurrentlyLiked) {
                    currentCard.likesCount - 1
                } else {
                    currentCard.likesCount + 1
                }
                
                val updatedCard = currentCard.copy(likesCount = newLikesCount)
                travelCardRepository.updateTravelCard(updatedCard)
                _travelCard.value = updatedCard
                _isLiked.value = !isCurrentlyLiked
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleSave() {
        _isSaved.value = !(_isSaved.value ?: false)
        // TODO: Implement save/unsave logic with local storage
    }

    fun shareCard() {
        // TODO: Implement sharing functionality
    }
}

class TravelCardDetailViewModelFactory(
    private val travelCardRepository: TravelCardRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TravelCardDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TravelCardDetailViewModel(travelCardRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
