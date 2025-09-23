package com.example.places.ui.entrypreview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.places.PlacesApplication
import com.example.places.R
import com.example.places.databinding.ActivityEntryPreviewBinding
import com.example.places.data.entity.PublishedActivity
import com.example.places.data.repository.PublishedActivityRepository
import java.util.*

class EntryPreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEntryPreviewBinding
    private lateinit var publishedActivityRepository: PublishedActivityRepository

    companion object {
        private const val EXTRA_LOCATION = "extra_location"
        private const val EXTRA_DATE = "extra_date"
        private const val EXTRA_NOTES = "extra_notes"
        private const val EXTRA_ACTIVITY_NAME = "extra_activity_name"
        private const val EXTRA_ACTIVITY_DESCRIPTION = "extra_activity_description"
        private const val EXTRA_ACTIVITY_TIME = "extra_activity_time"
        private const val EXTRA_ACTIVITY_DURATION = "extra_activity_duration"

        fun newIntent(
            context: Context,
            location: String,
            date: String,
            notes: String,
            activityName: String,
            activityDescription: String,
            activityTime: String,
            activityDuration: String
        ): Intent {
            return Intent(context, EntryPreviewActivity::class.java).apply {
                putExtra(EXTRA_LOCATION, location)
                putExtra(EXTRA_DATE, date)
                putExtra(EXTRA_NOTES, notes)
                putExtra(EXTRA_ACTIVITY_NAME, activityName)
                putExtra(EXTRA_ACTIVITY_DESCRIPTION, activityDescription)
                putExtra(EXTRA_ACTIVITY_TIME, activityTime)
                putExtra(EXTRA_ACTIVITY_DURATION, activityDuration)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntryPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRepository()
        loadDataFromIntent()
        setupClickListeners()
        setupBottomNavigation()
    }

    private fun setupRepository() {
        val application = application as PlacesApplication
        publishedActivityRepository = application.publishedActivityRepository
    }

    private fun loadDataFromIntent() {
        val location = intent.getStringExtra(EXTRA_LOCATION) ?: "Colombo- Sri Lanka"
        val date = intent.getStringExtra(EXTRA_DATE) ?: "21st Saturday November 2025"
        val notes = intent.getStringExtra(EXTRA_NOTES) ?: "Lorem ipsum dolor sit amet consectetur. Posuere tincidunt nec ipsum tincidunt nisl a. Urna viverra turpis mauris dictumst augue."
        val activityName = intent.getStringExtra(EXTRA_ACTIVITY_NAME) ?: "Colombo City Center"
        val activityDescription = intent.getStringExtra(EXTRA_ACTIVITY_DESCRIPTION) ?: "Lorem ipsum dolor sit amet consectetur. Porta id facilisis lorem id porttitor."
        val activityTime = intent.getStringExtra(EXTRA_ACTIVITY_TIME) ?: "Toad, 9.00AM"
        val activityDuration = intent.getStringExtra(EXTRA_ACTIVITY_DURATION) ?: "1.00pm-2.30pm"

        binding.apply {
            tvLocation.text = location
            tvDate.text = date
            tvDescription.text = notes
            tvActivityName.text = activityName
            tvActivityDescription.text = activityDescription
            tvActivityTime.text = activityTime
            tvActivityDuration.text = activityDuration
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            ivEdit.setOnClickListener {
                // TODO: Navigate back to edit main entry
                Toast.makeText(this@EntryPreviewActivity, "Edit entry functionality coming soon", Toast.LENGTH_SHORT).show()
            }

            ivDelete.setOnClickListener {
                // TODO: Delete entire entry
                Toast.makeText(this@EntryPreviewActivity, "Delete entry functionality coming soon", Toast.LENGTH_SHORT).show()
            }

            ivEditActivity.setOnClickListener {
                // TODO: Navigate back to edit activity
                Toast.makeText(this@EntryPreviewActivity, "Edit activity functionality coming soon", Toast.LENGTH_SHORT).show()
            }

            ivDeleteActivity.setOnClickListener {
                // TODO: Delete this activity
                Toast.makeText(this@EntryPreviewActivity, "Delete activity functionality coming soon", Toast.LENGTH_SHORT).show()
            }

            btnPublish.setOnClickListener {
                publishEntry()
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_feed -> {
                    startActivity(Intent(this, com.example.places.ui.feed.FeedActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_messages -> {
                    startActivity(Intent(this, com.example.places.ui.messages.MessagesActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_explore -> {
                    startActivity(Intent(this, com.example.places.ui.explore.ExploreActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_notifications -> {
                    startActivity(Intent(this, com.example.places.ui.notifications.NotificationsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, com.example.places.ui.profile.ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun publishEntry() {
        // Get current user info from shared preferences or user repository
        val currentUser = getCurrentUser() // You'll need to implement this method
        
        // Create a published activity from the current entry data
        val publishedActivity = PublishedActivity(
            id = System.currentTimeMillis().toString(),
            username = currentUser.displayName ?: "Sophia Carter",
            userProfileImage = currentUser.profileImageUrl,
            location = binding.tvLocation.text.toString(),
            date = binding.tvDate.text.toString(),
            description = binding.tvDescription.text.toString(),
            activityTitle = binding.tvActivityName.text.toString(),
            activityDescription = binding.tvActivityDescription.text.toString(),
            activityTime = binding.tvActivityDuration.text.toString(),
            heroImage = null,
            activityImage = null,
            likeCount = 0,
            commentCount = 0,
            shareCount = 0,
            isLiked = false,
            isBookmarked = false,
            createdAt = Date(),
            updatedAt = Date()
        )
        
        // Save to Room database
        lifecycleScope.launch {
            publishedActivityRepository.insertPublishedActivity(publishedActivity)
            Toast.makeText(this@EntryPreviewActivity, "Entry published successfully!", Toast.LENGTH_SHORT).show()
            
            // Navigate to profile to show the newly published activity
            val intent = Intent(this@EntryPreviewActivity, com.example.places.ui.profile.ProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
    
    private fun getCurrentUser(): com.example.places.data.entity.User {
        // Try to get the actual current user from the repository
        val userRepository = (application as PlacesApplication).userRepository
        return try {
            // This would be a suspend function call in a real implementation
            // For now, return a consistent default user that matches what's used elsewhere
            com.example.places.data.entity.User(
                id = "user1",
                email = "sophia.carter@example.com",
                displayName = "Sophia Carter",
                profileImageUrl = null,
                bio = "Travel enthusiast | Sharing my adventures",
                createdAt = Date(),
                updatedAt = Date()
            )
        } catch (e: Exception) {
            // Fallback to default user
            com.example.places.data.entity.User(
                id = "user1",
                email = "sophia.carter@example.com",
                displayName = "Sophia Carter",
                profileImageUrl = null,
                bio = "Travel enthusiast | Sharing my adventures",
                createdAt = Date(),
                updatedAt = Date()
            )
        }
    }
}
