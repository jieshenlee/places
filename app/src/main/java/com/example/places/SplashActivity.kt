package com.example.places

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    
    private val splashTimeOut: Long = 3000 // 3 seconds
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        // Hide action bar
        supportActionBar?.hide()
        
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if user is already logged in
            val userRepository = (application as PlacesApplication).userRepository
            if (userRepository.isUserLoggedIn()) {
                // User is logged in, go to main activity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                // User is not logged in, go to onboarding
                val intent = Intent(this, OnboardingActivity1::class.java)
                startActivity(intent)
            }
            finish()
        }, splashTimeOut)
    }
}
