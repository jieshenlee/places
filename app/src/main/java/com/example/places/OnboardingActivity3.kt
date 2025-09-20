package com.example.places

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class OnboardingActivity3 : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_3)
        
        // Hide action bar
        supportActionBar?.hide()
        
        val btnSignUp = findViewById<Button>(R.id.btn_sign_up)
        val btnLogIn = findViewById<Button>(R.id.btn_log_in)
        
        btnSignUp.setOnClickListener {
            // Navigate to main app or sign up screen
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        
        btnLogIn.setOnClickListener {
            // Navigate to main app or login screen
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
