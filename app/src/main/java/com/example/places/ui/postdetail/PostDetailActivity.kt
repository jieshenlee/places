package com.example.places.ui.postdetail

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.places.R
import com.example.places.databinding.ActivityPostDetailBinding
import com.example.places.ui.addactivity.AddActivityActivity
import com.example.places.ui.explore.ExploreActivity
import com.example.places.ui.feed.FeedActivity
import com.example.places.ui.messages.MessagesActivity
import com.example.places.ui.notifications.NotificationsActivity
import com.example.places.ui.profile.ProfileActivity

class PostDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPostDetailBinding
    
    companion object {
        const val EXTRA_POST_ID = "extra_post_id"
        const val EXTRA_USERNAME = "extra_username"
        const val EXTRA_LOCATION = "extra_location"
        const val EXTRA_DATE = "extra_date"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_IMAGE_URL = "extra_image_url"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        setupBottomNavigation()
        setupFAB()
        loadPostData()
    }
    
    private fun setupUI() {
        binding.buttonBack.setOnClickListener {
            finish()
        }
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_feed -> {
                    startActivity(Intent(this, FeedActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_messages -> {
                    startActivity(Intent(this, MessagesActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_explore -> {
                    startActivity(Intent(this, ExploreActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_notifications -> {
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
    
    private fun setupFAB() {
        binding.fabAddPost.setOnClickListener {
            startActivity(Intent(this, AddActivityActivity::class.java))
        }
    }
    
    private fun loadPostData() {
        // Get data from intent
        val username = intent.getStringExtra(EXTRA_USERNAME) ?: "Sherwin Jieshen Li"
        val location = intent.getStringExtra(EXTRA_LOCATION) ?: "Galle - Sri Lanka"
        val date = intent.getStringExtra(EXTRA_DATE) ?: "1st November 2025"
        val description = intent.getStringExtra(EXTRA_DESCRIPTION) ?: 
            "Lorem ipsum dolor sit amet consectetur. Tellus ultrices velit sed faucibus."
        val imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL)
        
        // Set user info
        binding.textViewUsername.text = username
        binding.textViewLocation.text = location
        binding.textViewDate.text = date
        binding.textViewDescription.text = description
        
        // Load profile image
        binding.imageViewProfile.setImageResource(R.drawable.ic_person)
        
        // Load hero image
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_ocean_dock)
                .error(R.drawable.ic_ocean_dock)
                .into(binding.imageViewHero)
        } else {
            // Use sample image based on username
            val sampleImage = when (username) {
                "Sherwin Jieshen Li" -> R.drawable.ic_ocean_dock
                "Ethan Ramirez" -> R.drawable.ic_mountain_landscape
                "Priya Kapoor" -> R.drawable.ic_places_logo
                else -> R.drawable.ic_ocean_dock
            }
            binding.imageViewHero.setImageResource(sampleImage)
        }
        
        setupActivityInteractions()
    }
    
    private fun setupActivityInteractions() {
        // Setup click listeners for activity cards
        // This would typically be done with RecyclerView for dynamic content
        
        // For now, we'll add simple toast messages for interactions
        // In a real app, these would handle like/comment/share/bookmark actions
        
        // You can add specific click listeners here for the activity cards
        // For example, to handle likes, comments, shares, and bookmarks
        
        Toast.makeText(this, "Post detail loaded for ${binding.textViewUsername.text}", Toast.LENGTH_SHORT).show()
    }
}
