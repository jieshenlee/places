package com.example.places.ui.feed

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import com.example.places.PlacesApplication
import com.example.places.R
import com.example.places.data.entity.FeedPost
import com.example.places.databinding.ActivityFeedBinding
import com.example.places.ui.addactivity.AddActivityActivity
import com.example.places.ui.explore.ExploreActivity
import com.example.places.ui.messages.MessagesActivity
import com.example.places.ui.notifications.NotificationsActivity
import com.example.places.ui.profile.ProfileActivity
import com.example.places.data.entity.PublishedActivity
import com.example.places.data.repository.PublishedActivityRepository

class FeedActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityFeedBinding
    private lateinit var viewModel: FeedViewModel
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var publishedActivityAdapter: PublishedActivityAdapter
    private lateinit var publishedActivityRepository: PublishedActivityRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRepository()
        setupViewModel()
        setupRecyclerView()
        setupBottomNavigation()
        setupFAB()
        observeViewModel()
        loadInitialData()
    }
    
    private fun setupRepository() {
        val application = application as PlacesApplication
        publishedActivityRepository = application.publishedActivityRepository
    }
    
    private fun setupViewModel() {
        val application = application as PlacesApplication
        val repository = application.feedPostRepository
        
        viewModel = ViewModelProvider(
            this,
            FeedViewModelFactory(repository)
        )[FeedViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        publishedActivityAdapter = PublishedActivityAdapter(
            onLikeClick = { activity -> handleLikeClick(activity) },
            onCommentClick = { activity -> handleCommentClick(activity) },
            onShareClick = { activity -> handleShareClick(activity) },
            onBookmarkClick = { activity -> handleBookmarkClick(activity) },
            onActivityClick = { activity -> handleActivityClick(activity) },
            onEditClick = { activity -> handleEditClick(activity) }
        )
        
        binding.recyclerViewFeed.apply {
            layoutManager = LinearLayoutManager(this@FeedActivity)
            adapter = publishedActivityAdapter
        }
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_feed
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_feed -> {
                    // Already on feed page
                    true
                }
                R.id.nav_messages -> {
                    startActivity(Intent(this, MessagesActivity::class.java))
                    true
                }
                R.id.nav_explore -> {
                    startActivity(Intent(this, ExploreActivity::class.java))
                    true
                }
                R.id.nav_notifications -> {
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
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
    
    private fun observeViewModel() {
        // Observe published activities from Room database
        publishedActivityRepository.getAllPublishedActivities().observe(this) { activities ->
            publishedActivityAdapter.submitList(activities)
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }
    
    private fun loadInitialData() {
        // Load sample data if database is empty
        lifecycleScope.launch {
            publishedActivityRepository.insertSampleData()
        }
    }
    
    
    private fun handleLikeClick(activity: PublishedActivity) {
        lifecycleScope.launch {
            publishedActivityRepository.toggleLike(activity.id, activity.likeCount, activity.isLiked)
            Toast.makeText(this@FeedActivity, "Liked ${activity.activityTitle}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun handleCommentClick(activity: PublishedActivity) {
        // TODO: Navigate to comments screen
        Toast.makeText(this, "Comments for ${activity.activityTitle}", Toast.LENGTH_SHORT).show()
    }
    
    private fun handleShareClick(activity: PublishedActivity) {
        lifecycleScope.launch {
            publishedActivityRepository.incrementShareCount(activity.id)
            Toast.makeText(this@FeedActivity, "Share ${activity.activityTitle}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun handleBookmarkClick(activity: PublishedActivity) {
        lifecycleScope.launch {
            publishedActivityRepository.toggleBookmark(activity.id, activity.isBookmarked)
            Toast.makeText(this@FeedActivity, "Bookmarked ${activity.activityTitle}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun handleActivityClick(activity: PublishedActivity) {
        // Navigate to activity detail screen
        val intent = com.example.places.ui.detail.ActivityDetailActivity.newIntent(this, activity, "feed")
        startActivity(intent)
    }
    
    private fun handleEditClick(activity: PublishedActivity) {
        // Navigate to edit screen for the published activity
        Toast.makeText(this, "Edit ${activity.activityTitle}", Toast.LENGTH_SHORT).show()
        // TODO: Navigate to CreateActivityActivity with pre-filled data for editing
        // val intent = Intent(this, CreateActivityActivity::class.java)
        // intent.putExtra("EDIT_MODE", true)
        // intent.putExtra("ACTIVITY_DATA", activity)
        // startActivity(intent)
    }
    
    private fun handleCommentClick(post: FeedPost) {
        // Navigate to comments or show comment dialog
        Toast.makeText(this, "Comments for ${post.username}'s post", Toast.LENGTH_SHORT).show()
    }
    
    private fun handleShareClick(post: FeedPost) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check out ${post.username}'s travel post: ${post.description}")
        }
        startActivity(Intent.createChooser(shareIntent, "Share post"))
    }
    
    private fun handleUserClick(post: FeedPost) {
        // Navigate to user profile
        Toast.makeText(this, "View ${post.username}'s profile", Toast.LENGTH_SHORT).show()
    }
    
    private fun handlePostClick(post: FeedPost) {
        val intent = Intent(this, com.example.places.ui.postdetail.PostDetailActivity::class.java).apply {
            putExtra(com.example.places.ui.postdetail.PostDetailActivity.EXTRA_POST_ID, post.id)
            putExtra(com.example.places.ui.postdetail.PostDetailActivity.EXTRA_USERNAME, post.username)
            putExtra(com.example.places.ui.postdetail.PostDetailActivity.EXTRA_LOCATION, post.location)
            putExtra(com.example.places.ui.postdetail.PostDetailActivity.EXTRA_DATE, post.createdAt.toString())
            putExtra(com.example.places.ui.postdetail.PostDetailActivity.EXTRA_DESCRIPTION, post.description)
            putExtra(com.example.places.ui.postdetail.PostDetailActivity.EXTRA_IMAGE_URL, post.imageUrl)
        }
        startActivity(intent)
    }
}
