package com.example.places.ui.feed

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.places.PlacesApplication
import com.example.places.R
import com.example.places.data.entity.FeedPost
import com.example.places.databinding.ActivityFeedBinding
import com.example.places.ui.addactivity.AddActivityActivity
import com.example.places.ui.explore.ExploreActivity
import com.example.places.ui.messages.MessagesActivity
import com.example.places.ui.notifications.NotificationsActivity
import com.example.places.ui.profile.ProfileActivity

class FeedActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityFeedBinding
    private lateinit var viewModel: FeedViewModel
    private lateinit var feedAdapter: FeedAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewModel()
        setupRecyclerView()
        setupBottomNavigation()
        setupFAB()
        observeViewModel()
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
        feedAdapter = FeedAdapter(
            onLikeClick = { post -> viewModel.toggleLike(post) },
            onCommentClick = { post -> handleCommentClick(post) },
            onShareClick = { post -> handleShareClick(post) },
            onBookmarkClick = { post -> viewModel.toggleBookmark(post) },
            onUserClick = { post -> handleUserClick(post) },
            onPostClick = { post -> handlePostClick(post) }
        )
        
        binding.recyclerViewFeed.apply {
            layoutManager = LinearLayoutManager(this@FeedActivity)
            adapter = feedAdapter
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
        viewModel.feedPosts.observe(this) { posts ->
            feedAdapter.submitList(posts)
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            // Handle loading state if needed
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
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
