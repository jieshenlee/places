package com.example.places.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.places.R
import com.example.places.data.entity.PublishedActivity
import com.example.places.databinding.ActivityDetailBinding
import com.example.places.ui.addactivity.AddActivityActivity
import com.example.places.ui.explore.ExploreActivity
import com.example.places.ui.feed.FeedActivity
import com.example.places.ui.messages.MessagesActivity
import com.example.places.ui.notifications.NotificationsActivity
import com.example.places.ui.profile.ProfileActivity

class ActivityDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityDetailBinding
    private var currentActivity: PublishedActivity? = null
    
    companion object {
        private const val EXTRA_ACTIVITY_ID = "extra_activity_id"
        private const val EXTRA_USERNAME = "extra_username"
        private const val EXTRA_LOCATION = "extra_location"
        private const val EXTRA_DATE = "extra_date"
        private const val EXTRA_DESCRIPTION = "extra_description"
        private const val EXTRA_ACTIVITY_TITLE = "extra_activity_title"
        private const val EXTRA_ACTIVITY_DESCRIPTION = "extra_activity_description"
        private const val EXTRA_ACTIVITY_TIME = "extra_activity_time"
        private const val EXTRA_HERO_IMAGE = "extra_hero_image"
        private const val EXTRA_ACTIVITY_IMAGE = "extra_activity_image"
        private const val EXTRA_USER_PROFILE_IMAGE = "extra_user_profile_image"
        private const val EXTRA_LIKE_COUNT = "extra_like_count"
        private const val EXTRA_COMMENT_COUNT = "extra_comment_count"
        private const val EXTRA_SHARE_COUNT = "extra_share_count"
        private const val EXTRA_IS_LIKED = "extra_is_liked"
        private const val EXTRA_IS_BOOKMARKED = "extra_is_bookmarked"
        private const val EXTRA_SOURCE_SCREEN = "extra_source_screen"
        
        fun newIntent(context: Context, activity: PublishedActivity, sourceScreen: String = "feed"): Intent {
            return Intent(context, ActivityDetailActivity::class.java).apply {
                putExtra(EXTRA_ACTIVITY_ID, activity.id)
                putExtra(EXTRA_USERNAME, activity.username)
                putExtra(EXTRA_LOCATION, activity.location)
                putExtra(EXTRA_DATE, activity.date)
                putExtra(EXTRA_DESCRIPTION, activity.description)
                putExtra(EXTRA_ACTIVITY_TITLE, activity.activityTitle)
                putExtra(EXTRA_ACTIVITY_DESCRIPTION, activity.activityDescription)
                putExtra(EXTRA_ACTIVITY_TIME, activity.activityTime)
                putExtra(EXTRA_HERO_IMAGE, activity.heroImage)
                putExtra(EXTRA_ACTIVITY_IMAGE, activity.activityImage)
                putExtra(EXTRA_USER_PROFILE_IMAGE, activity.userProfileImage)
                putExtra(EXTRA_LIKE_COUNT, activity.likeCount)
                putExtra(EXTRA_COMMENT_COUNT, activity.commentCount)
                putExtra(EXTRA_SHARE_COUNT, activity.shareCount)
                putExtra(EXTRA_IS_LIKED, activity.isLiked)
                putExtra(EXTRA_IS_BOOKMARKED, activity.isBookmarked)
                putExtra(EXTRA_SOURCE_SCREEN, sourceScreen)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Hide action bar
        supportActionBar?.hide()
        
        loadActivityData()
        setupClickListeners()
        setupBottomNavigation()
    }
    
    private fun loadActivityData() {
        // Create PublishedActivity object from intent extras
        currentActivity = PublishedActivity(
            id = intent.getStringExtra(EXTRA_ACTIVITY_ID) ?: "",
            username = intent.getStringExtra(EXTRA_USERNAME) ?: "",
            location = intent.getStringExtra(EXTRA_LOCATION) ?: "",
            date = intent.getStringExtra(EXTRA_DATE) ?: "",
            description = intent.getStringExtra(EXTRA_DESCRIPTION) ?: "",
            activityTitle = intent.getStringExtra(EXTRA_ACTIVITY_TITLE) ?: "",
            activityDescription = intent.getStringExtra(EXTRA_ACTIVITY_DESCRIPTION) ?: "",
            activityTime = intent.getStringExtra(EXTRA_ACTIVITY_TIME) ?: "",
            heroImage = intent.getStringExtra(EXTRA_HERO_IMAGE),
            activityImage = intent.getStringExtra(EXTRA_ACTIVITY_IMAGE),
            userProfileImage = intent.getStringExtra(EXTRA_USER_PROFILE_IMAGE),
            likeCount = intent.getIntExtra(EXTRA_LIKE_COUNT, 0),
            commentCount = intent.getIntExtra(EXTRA_COMMENT_COUNT, 0),
            shareCount = intent.getIntExtra(EXTRA_SHARE_COUNT, 0),
            isLiked = intent.getBooleanExtra(EXTRA_IS_LIKED, false),
            isBookmarked = intent.getBooleanExtra(EXTRA_IS_BOOKMARKED, false)
        )
        
        currentActivity?.let { activity ->
            // Set text data
            binding.tvUsername.text = activity.username
            binding.tvLocation.text = activity.location
            binding.tvDate.text = activity.date
            binding.tvDescription.text = activity.description
            binding.tvActivityTitle.text = activity.activityTitle
            binding.tvActivityDescription.text = activity.activityDescription
            binding.tvActivityTime.text = activity.activityTime
            binding.tvLikeCount.text = activity.likeCount.toString()
            binding.tvCommentCount.text = activity.commentCount.toString()
            binding.tvShareCount.text = activity.shareCount.toString()
            
            // Load images using Glide
            activity.userProfileImage?.let { imageUrl ->
                Glide.with(this)
                    .load(imageUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_person_placeholder)
                    .into(binding.ivUserProfile)
            }
            
            activity.heroImage?.let { imageUrl ->
                Glide.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(binding.ivLeftImage)
                
                // Use the same image for right image for now
                Glide.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(binding.ivRightImage)
            }
            
            activity.activityImage?.let { imageUrl ->
                Glide.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(binding.ivActivityImage)
            }
            
            // Update like state
            binding.ivLike.setImageResource(
                if (activity.isLiked) R.drawable.ic_favorite 
                else R.drawable.ic_favorite_border
            )
            
            // Update bookmark state
            binding.ivBookmark.setImageResource(
                if (activity.isBookmarked) R.drawable.ic_bookmark 
                else R.drawable.ic_bookmark_border
            )
        }
    }
    
    private fun setupClickListeners() {
        binding.layoutLike.setOnClickListener {
            currentActivity?.let { activity ->
                // Create a copy with updated like state
                val updatedActivity = activity.copy(
                    isLiked = !activity.isLiked,
                    likeCount = if (!activity.isLiked) activity.likeCount + 1 else activity.likeCount - 1
                )
                currentActivity = updatedActivity
                
                // Update UI
                binding.ivLike.setImageResource(
                    if (updatedActivity.isLiked) R.drawable.ic_favorite 
                    else R.drawable.ic_favorite_border
                )
                binding.tvLikeCount.text = updatedActivity.likeCount.toString()
                
                Toast.makeText(this, 
                    if (updatedActivity.isLiked) "Liked!" else "Unliked", 
                    Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.layoutComment.setOnClickListener {
            currentActivity?.let { activity ->
                Toast.makeText(this, "Comment on ${activity.activityTitle}", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.layoutShare.setOnClickListener {
            currentActivity?.let { activity ->
                Toast.makeText(this, "Share ${activity.activityTitle}", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.ivBookmark.setOnClickListener {
            currentActivity?.let { activity ->
                // Create a copy with updated bookmark state
                val updatedActivity = activity.copy(isBookmarked = !activity.isBookmarked)
                currentActivity = updatedActivity
                
                // Update UI
                binding.ivBookmark.setImageResource(
                    if (updatedActivity.isBookmarked) R.drawable.ic_bookmark 
                    else R.drawable.ic_bookmark_border
                )
                
                Toast.makeText(this, 
                    if (updatedActivity.isBookmarked) "Bookmarked!" else "Removed from bookmarks", 
                    Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.ivEdit.setOnClickListener {
            currentActivity?.let { activity ->
                Toast.makeText(this, "Edit ${activity.activityTitle}", Toast.LENGTH_SHORT).show()
                // TODO: Navigate to edit activity screen
            }
        }
    }
    
    private fun setupBottomNavigation() {
        // Get the source screen to determine which navigation item should be selected
        val sourceScreen = intent.getStringExtra(EXTRA_SOURCE_SCREEN) ?: "feed"
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_feed -> {
                    startActivity(Intent(this, FeedActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_explore -> {
                    startActivity(Intent(this, ExploreActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_messages -> {
                    startActivity(Intent(this, MessagesActivity::class.java))
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
        
        // Set current item based on where user came from, but don't trigger the listener
        val selectedItemId = when (sourceScreen) {
            "profile" -> R.id.nav_profile
            "explore" -> R.id.nav_explore
            "messages" -> R.id.nav_messages
            "notifications" -> R.id.nav_notifications
            else -> R.id.nav_feed // default to feed
        }
        
        // Temporarily remove listener to avoid triggering navigation
        binding.bottomNavigation.setOnItemSelectedListener(null)
        binding.bottomNavigation.selectedItemId = selectedItemId
        
        // Re-add the listener after setting the selected item
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_feed -> {
                    startActivity(Intent(this, FeedActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_explore -> {
                    startActivity(Intent(this, ExploreActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_messages -> {
                    startActivity(Intent(this, MessagesActivity::class.java))
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
}