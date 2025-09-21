package com.example.places.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.places.data.repository.UserRepository
import kotlinx.coroutines.launch

class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    private val _signUpSuccess = MutableLiveData<Boolean>()
    val signUpSuccess: LiveData<Boolean> = _signUpSuccess
    
    fun signUp(displayName: String, email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = ""
        
        viewModelScope.launch {
            try {
                val result = userRepository.registerUser(email, password, displayName)
                if (result != null) {
                    _signUpSuccess.value = true
                } else {
                    _errorMessage.value = "Registration failed. Email might already be in use."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Registration failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = ""
    }
}

class SignUpViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignUpViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}