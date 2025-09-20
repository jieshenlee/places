package com.example.places.ui.notifications

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.places.PlacesApplication
import com.example.places.R
import com.example.places.databinding.ActivityNotificationsBinding
import com.example.places.ui.detail.TravelCardDetailActivity

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var todayAdapter: NotificationsAdapter
    private lateinit var yesterdayAdapter: NotificationsAdapter
    
    private val viewModel: NotificationsViewModel by viewModels {
        NotificationsViewModelFactory(
            (application as PlacesApplication).notificationRepository,
            (application as PlacesApplication).userRepository
        )
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, NotificationsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupRecyclerViews()
        observeViewModel()
        setupClickListeners()
        
        viewModel.loadNotifications()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerViews() {
        todayAdapter = NotificationsAdapter { notification ->
            viewModel.handleNotificationClick(notification)
            handleNotificationNavigation(notification)
        }
        
        yesterdayAdapter = NotificationsAdapter { notification ->
            viewModel.handleNotificationClick(notification)
            handleNotificationNavigation(notification)
        }
        
        binding.rvTodayNotifications.apply {
            adapter = todayAdapter
            layoutManager = LinearLayoutManager(this@NotificationsActivity)
        }
        
        binding.rvYesterdayNotifications.apply {
            adapter = yesterdayAdapter
            layoutManager = LinearLayoutManager(this@NotificationsActivity)
        }
    }

    private fun observeViewModel() {
        viewModel.todayNotifications.observe(this) { notifications ->
            todayAdapter.submitList(notifications)
            updateSectionVisibility()
        }

        viewModel.yesterdayNotifications.observe(this) { notifications ->
            yesterdayAdapter.submitList(notifications)
            updateSectionVisibility()
        }

        viewModel.hasNotifications.observe(this) { hasNotifications ->
            if (hasNotifications) {
                binding.layoutEmptyState.visibility = android.view.View.GONE
            } else {
                binding.layoutEmptyState.visibility = android.view.View.VISIBLE
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            // TODO: Show/hide loading indicator
        }
    }

    private fun updateSectionVisibility() {
        val todayNotifications = viewModel.todayNotifications.value ?: emptyList()
        val yesterdayNotifications = viewModel.yesterdayNotifications.value ?: emptyList()
        
        // Show/hide today section
        if (todayNotifications.isNotEmpty()) {
            binding.tvTodayHeader.visibility = android.view.View.VISIBLE
            binding.rvTodayNotifications.visibility = android.view.View.VISIBLE
        } else {
            binding.tvTodayHeader.visibility = android.view.View.GONE
            binding.rvTodayNotifications.visibility = android.view.View.GONE
        }
        
        // Show/hide yesterday section
        if (yesterdayNotifications.isNotEmpty()) {
            binding.tvYesterdayHeader.visibility = android.view.View.VISIBLE
            binding.rvYesterdayNotifications.visibility = android.view.View.VISIBLE
        } else {
            binding.tvYesterdayHeader.visibility = android.view.View.GONE
            binding.rvYesterdayNotifications.visibility = android.view.View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, com.example.places.ui.addactivity.AddActivityActivity::class.java))
        }
        
        setupBottomNavigation()
    }

    private fun handleNotificationNavigation(notification: com.example.places.data.entity.Notification) {
        when (notification.type) {
            com.example.places.data.entity.NotificationType.LIKE,
            com.example.places.data.entity.NotificationType.COMMENT -> {
                notification.relatedEntityId?.let { travelCardId ->
                    val intent = TravelCardDetailActivity.newIntent(this, travelCardId)
                    startActivity(intent)
                }
            }
            com.example.places.data.entity.NotificationType.FOLLOW -> {
                // TODO: Navigate to user profile
            }
            else -> {
                // Handle other notification types
            }
        }
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_notifications
        
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
                    // Already on notifications page
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
}
