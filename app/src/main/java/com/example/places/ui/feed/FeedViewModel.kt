package com.example.places.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.places.data.entity.FeedPost
import com.example.places.data.repository.FeedPostRepository
import kotlinx.coroutines.launch

class FeedViewModel(private val repository: FeedPostRepository) : ViewModel() {
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    val feedPosts: LiveData<List<FeedPost>> = repository.getAllPosts()
    
    init {
        // No longer loading sample data automatically
        // Feed will be populated when users create activities
    }

    private fun loadSampleData() {
        // Removed automatic sample data loading
        // Users start with empty feed
    }
    
    fun toggleLike(post: FeedPost) {
        viewModelScope.launch {
            try {
                repository.toggleLike(post.id, post.isLiked, post.likeCount)
            } catch (e: Exception) {
                _error.value = "Failed to update like: ${e.message}"
            }
        }
    }
    
    fun toggleBookmark(post: FeedPost) {
        viewModelScope.launch {
            try {
                repository.toggleBookmark(post.id, post.isBookmarked)
            } catch (e: Exception) {
                _error.value = "Failed to update bookmark: ${e.message}"
            }
        }
    }
    
    fun refreshFeed() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // In a real app, this would fetch from API
                repository.insertSampleData()
            } catch (e: Exception) {
                _error.value = "Failed to refresh feed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}
