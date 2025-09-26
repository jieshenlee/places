package com.example.places

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.places.databinding.ActivityEditProfileBinding
import com.example.places.data.entity.User
import com.example.places.data.repository.UserRepository
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var userRepository: UserRepository
    private var currentUser: User? = null
    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                loadProfileImage(uri.toString())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = (application as PlacesApplication).userRepository
        
        setupUI()
        loadCurrentUser()
    }

    private fun setupUI() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnChangePhoto.setOnClickListener {
            openImagePicker()
        }

        binding.btnSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun loadCurrentUser() {
        lifecycleScope.launch {
            try {
                currentUser = userRepository.getCurrentUser()
                currentUser?.let { user ->
                    populateFields(user)
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditProfileActivity, "Error loading profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun populateFields(user: User) {
        binding.etDisplayName.setText(user.displayName)
        binding.etEmail.setText(user.email)
        binding.etBio.setText(user.bio)
        
        // Load profile image
        if (!user.profileImageUrl.isNullOrEmpty()) {
            loadProfileImage(user.profileImageUrl)
        }
    }

    private fun loadProfileImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_person_placeholder)
            .error(R.drawable.ic_person_placeholder)
            .centerCrop()
            .into(binding.ivProfileImage)
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun saveProfile() {
        val displayName = binding.etDisplayName.text.toString().trim()
        val bio = binding.etBio.text.toString().trim()

        if (displayName.isEmpty()) {
            binding.tilDisplayName.error = "Display name is required"
            return
        }

        if (displayName.length < 2) {
            binding.tilDisplayName.error = "Display name must be at least 2 characters"
            return
        }

        binding.tilDisplayName.error = null
        showLoading(true)

        lifecycleScope.launch {
            try {
                currentUser?.let { user ->
                    val updatedUser = user.copy(
                        displayName = displayName,
                        bio = bio,
                        profileImageUrl = selectedImageUri?.toString() ?: user.profileImageUrl
                    )

                    val success = userRepository.updateUser(updatedUser)
                    
                    Toast.makeText(this@EditProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    
                    // Return result to indicate profile was updated
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditProfileActivity, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnSave.isEnabled = !show
        binding.btnChangePhoto.isEnabled = !show
        binding.etDisplayName.isEnabled = !show
        binding.etBio.isEnabled = !show
    }

    companion object {
        fun newIntent(activity: Activity): Intent {
            return Intent(activity, EditProfileActivity::class.java)
        }
    }
}