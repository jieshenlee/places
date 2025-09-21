package com.example.places.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.places.MainActivity
import com.example.places.databinding.ActivitySignupBinding
import com.example.places.PlacesApplication

class SignUpActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySignupBinding
    private val viewModel: SignUpViewModel by viewModels {
        SignUpViewModelFactory(
            (application as PlacesApplication).userRepository
        )
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        // Sign up button
        binding.btnSignUp.setOnClickListener {
            val displayName = binding.etDisplayName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            
            if (validateInput(displayName, email, password, confirmPassword)) {
                viewModel.signUp(displayName, email, password)
            }
        }
        
        // Login link
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    
    private fun validateInput(displayName: String, email: String, password: String, confirmPassword: String): Boolean {
        // Clear previous errors
        binding.tilDisplayName.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null
        
        var isValid = true
        
        // Validate display name
        if (displayName.isEmpty()) {
            binding.tilDisplayName.error = "Display name is required"
            isValid = false
        } else if (displayName.length < 2) {
            binding.tilDisplayName.error = "Display name must be at least 2 characters"
            isValid = false
        }
        
        // Validate email
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Please enter a valid email address"
            isValid = false
        }
        
        // Validate password
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            isValid = false
        }
        
        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.error = "Please confirm your password"
            isValid = false
        } else if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Passwords do not match"
            isValid = false
        }
        
        return isValid
    }
    
    private fun observeViewModel() {
        viewModel.isLoading.observe(this, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSignUp.isEnabled = !isLoading
        })
        
        viewModel.errorMessage.observe(this, Observer { error ->
            if (error.isNotEmpty()) {
                binding.tvError.text = error
                binding.tvError.visibility = View.VISIBLE
            } else {
                binding.tvError.visibility = View.GONE
            }
        })
        
        viewModel.signUpSuccess.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                // Navigate to main activity
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        })
    }
}