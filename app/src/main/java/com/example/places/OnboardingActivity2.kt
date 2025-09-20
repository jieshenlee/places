package com.example.places

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class OnboardingActivity2 : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_2)
        
        // Hide action bar
        supportActionBar?.hide()
        
        val btnJoinCommunity = findViewById<Button>(R.id.btn_join_community)
        btnJoinCommunity.setOnClickListener {
            val intent = Intent(this, OnboardingActivity3::class.java)
            startActivity(intent)
            finish()
        }
    }
}
