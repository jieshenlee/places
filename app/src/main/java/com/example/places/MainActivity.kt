package com.example.places

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.places.ui.feed.FeedActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check authentication status before setting up the UI
        checkAuthenticationStatus()
        
        // If user is authenticated, redirect to FeedActivity (main app)
        val intent = Intent(this, FeedActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun checkAuthenticationStatus() {
        val userRepository = (application as PlacesApplication).userRepository
        
        if (!userRepository.isUserLoggedIn()) {
            // User is not logged in, redirect to onboarding
            val intent = Intent(this, OnboardingActivity1::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}