package com.example.places.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.places.PlacesApplication
import com.example.places.R
import com.example.places.databinding.ActivityProfileBinding
import com.example.places.data.entity.PublishedActivity
import com.example.places.data.repository.PublishedActivityRepository
import com.example.places.ui.feed.PublishedActivityAdapter
import com.example.places.EditProfileActivity
import com.example.places.OnboardingActivity1
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var travelCardAdapter: TravelCardAdapter
    private lateinit var publishedActivityAdapter: PublishedActivityAdapter
    private lateinit var publishedActivityRepository: PublishedActivityRepository
    
    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(
            (application as PlacesApplication).userRepository,
            (application as PlacesApplication).travelCardRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the toolbar
        setSupportActionBar(binding.toolbar)

        setupRepository()
        setupUI()
        setupRecyclerView()
        setupPublishedActivitiesAdapter()
        observeViewModel()
        setupClickListeners()
        setupBottomNavigation()
        setupTabLayout()
        
        // Load sample published activities
        loadSampleData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performLogout() {
        lifecycleScope.launch {
            try {
                // Clear user session
                (application as PlacesApplication).userRepository.logoutUser()
                
                // Navigate to onboarding
                val intent = Intent(this@ProfileActivity, OnboardingActivity1::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                // Handle logout error if needed
                e.printStackTrace()
            }
        }
    }
    
    private fun loadSampleData() {
        // No longer loading sample data - users start with empty profile
        // Activities will be populated when users create them
    }

    private fun setupRepository() {
        val application = application as PlacesApplication
        publishedActivityRepository = application.publishedActivityRepository
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        
        // Set initial view mode to grid
        updateViewToggleButtons(isGridView = true)
        
        // Set default tab selection to Posts (index 0 now)
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0))
    }

    private fun setupRecyclerView() {
        travelCardAdapter = TravelCardAdapter({ travelCard ->
            // Handle travel card click - navigate to detail view
            val intent = com.example.places.ui.detail.TravelCardDetailActivity.newIntent(this, travelCard.id)
            startActivity(intent)
        }, isGridView = true)
        
        binding.rvTravelCards.apply {
            adapter = travelCardAdapter
            layoutManager = GridLayoutManager(this@ProfileActivity, 3)
        }
    }

    private fun setupPublishedActivitiesAdapter() {
        publishedActivityAdapter = PublishedActivityAdapter(
            onLikeClick = { activity -> handleLikeClick(activity) },
            onCommentClick = { activity -> handleCommentClick(activity) },
            onShareClick = { activity -> handleShareClick(activity) },
            onBookmarkClick = { activity -> handleBookmarkClick(activity) },
            onActivityClick = { activity -> handleActivityClick(activity) },
            onEditClick = { activity -> handleEditClick(activity) }
        )
    }

    private fun observeViewModel() {
        viewModel.currentUser.observe(this) { user ->
            user?.let { updateUserInfo(it) }
        }

        viewModel.userTravelCards.observe(this) { travelCards ->
            travelCardAdapter.submitList(travelCards)
            
            // Update trips count
            binding.tvTripsCount.text = travelCards.size.toString()
            
            // Update user stats if needed
            viewModel.currentUser.value?.let { user ->
                if (user.travelCardsCount != travelCards.size) {
                    viewModel.updateUserStats(user)
                }
            }
        }

        // Observe published activities for current user
        viewModel.currentUser.observe(this) { user ->
            user?.let { currentUser ->
                // Build possible usernames to match activities regardless of profile name changes
                val possibleUsernames = listOf(
                    currentUser.displayName,
                    currentUser.email.substringBefore("@")
                ).filterNotNull()

                // Observe all activities and filter by the possible usernames
                publishedActivityRepository.getAllPublishedActivities().observe(this) { allActivities ->
                    val userActivities = allActivities.filter { activity ->
                        possibleUsernames.any { username ->
                            activity.username.equals(username, ignoreCase = true)
                        }
                    }
                    publishedActivityAdapter.submitList(userActivities)
                }
            }
        }
    }

    private fun updateUserInfo(user: com.example.places.data.entity.User) {
        binding.apply {
            tvUserName.text = user.displayName
            tvUsername.text = "@${user.email.substringBefore("@")}"
            tvBio.text = user.bio ?: "Travel enthusiast | Sharing my adventures"
            
            tvTripsCount.text = user.travelCardsCount.toString()
            tvFollowersCount.text = user.followersCount.toString()
            tvFollowingCount.text = user.followingCount.toString()
            
            // Load profile image
            user.profileImageUrl?.let { imageUrl ->
                Glide.with(this@ProfileActivity)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_person_placeholder)
                    .error(R.drawable.ic_person_placeholder)
                    .circleCrop()
                    .into(ivProfileImage)
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnEditProfile.setOnClickListener {
                startActivity(EditProfileActivity.newIntent(this@ProfileActivity))
            }
            
            btnGridView.setOnClickListener {
                updateViewToggleButtons(isGridView = true)
                updateRecyclerViewLayout(isGridView = true)
                
                // Check which adapter is currently active
                when (binding.rvTravelCards.adapter) {
                    travelCardAdapter -> travelCardAdapter.setViewType(isGrid = true)
                    publishedActivityAdapter -> publishedActivityAdapter.setViewType(isGrid = true)
                }
            }
            
            btnListView.setOnClickListener {
                updateViewToggleButtons(isGridView = false)
                updateRecyclerViewLayout(isGridView = false)
                
                // Check which adapter is currently active
                when (binding.rvTravelCards.adapter) {
                    travelCardAdapter -> travelCardAdapter.setViewType(isGrid = false)
                    publishedActivityAdapter -> publishedActivityAdapter.setViewType(isGrid = false)
                }
            }
            
            fabAddTrip.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, com.example.places.ui.addactivity.AddActivityActivity::class.java))
            }
            
            // REMOVE THIS ENTIRE LISTENER - It's conflicting with the one in setupTabLayout()
            // tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            //     override fun onTabSelected(tab: TabLayout.Tab?) {
            //         when (tab?.position) {
            //             0 -> {
            //                 // Show user's trips
            //                 viewModel.userTravelCards.value?.let { cards ->
            //                     travelCardAdapter.submitList(cards)
            //                 }
            //             }
            //             1 -> {
            //                 // Show saved trips (TODO: implement saved functionality)
            //                 travelCardAdapter.submitList(emptyList())
            //             }
            //         }
            //     }
            //     
            //     override fun onTabUnselected(tab: TabLayout.Tab?) {}
            //     override fun onTabReselected(tab: TabLayout.Tab?) {}
            // })
        }
    }

    private fun updateViewToggleButtons(isGridView: Boolean) {
        binding.apply {
            if (isGridView) {
                btnGridView.setIconTintResource(R.color.md_theme_light_primary)
                btnGridView.setBackgroundColor(getColor(R.color.md_theme_light_primaryContainer))
                
                btnListView.setIconTintResource(R.color.md_theme_light_onSurfaceVariant)
                btnListView.setBackgroundColor(getColor(R.color.md_theme_light_surfaceVariant))
            } else {
                btnListView.setIconTintResource(R.color.md_theme_light_primary)
                btnListView.setBackgroundColor(getColor(R.color.md_theme_light_primaryContainer))
                
                btnGridView.setIconTintResource(R.color.md_theme_light_onSurfaceVariant)
                btnGridView.setBackgroundColor(getColor(R.color.md_theme_light_surfaceVariant))
            }
        }
    }

    private fun updateRecyclerViewLayout(isGridView: Boolean) {
        val layoutManager = if (isGridView) {
            GridLayoutManager(this, 3)
        } else {
            LinearLayoutManager(this)
        }
        binding.rvTravelCards.layoutManager = layoutManager
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_profile
        
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
                    // Already on profile page
                    true
                }
                else -> false
            }
        }
    }

    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> showPostsView()
                    1 -> showSavedView()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun showTripsView() {
        binding.rvTravelCards.adapter = travelCardAdapter
        binding.rvTravelCards.layoutManager = GridLayoutManager(this, 3)
        binding.btnGridView.visibility = android.view.View.VISIBLE
        binding.btnListView.visibility = android.view.View.VISIBLE
    }

    private fun showPostsView() {
        binding.rvTravelCards.adapter = publishedActivityAdapter
        binding.rvTravelCards.layoutManager = LinearLayoutManager(this)
        binding.btnGridView.visibility = android.view.View.VISIBLE
        binding.btnListView.visibility = android.view.View.VISIBLE
        
        // Load published activities for the current user when showing the Posts tab
        viewModel.currentUser.value?.let { user ->
            // Use multiple possible usernames to match activities
            val possibleUsernames = listOf(
                user.displayName ?: "Sophia Carter",
                "Sophia Carter", // Fallback default
                user.email.substringBefore("@") // Email-based username
            )
            
            // For now, let's show all published activities to debug the issue
            publishedActivityRepository.getAllPublishedActivities().observe(this) { allActivities ->
                // Filter activities that match any of the possible usernames
                val userActivities = allActivities.filter { activity ->
                    possibleUsernames.any { username -> 
                        activity.username.equals(username, ignoreCase = true)
                    }
                }
                
                publishedActivityAdapter.submitList(userActivities)
                
                // Debug: Log the activities to understand what's in the database
                android.util.Log.d("ProfileActivity", "Total activities: ${allActivities.size}")
                android.util.Log.d("ProfileActivity", "User activities: ${userActivities.size}")
                android.util.Log.d("ProfileActivity", "Looking for usernames: $possibleUsernames")
                allActivities.forEach { activity ->
                    android.util.Log.d("ProfileActivity", "Activity username: '${activity.username}'")
                }
            }
        }
    }

    private fun showSavedView() {
        // TODO: Implement saved items view
        binding.rvTravelCards.adapter = travelCardAdapter
        binding.rvTravelCards.layoutManager = GridLayoutManager(this, 3)
        binding.btnGridView.visibility = android.view.View.VISIBLE
        binding.btnListView.visibility = android.view.View.VISIBLE
    }

    // Published Activity Click Handlers
    private fun handleLikeClick(activity: PublishedActivity) {
        // TODO: Implement like functionality
        android.widget.Toast.makeText(this, "Liked ${activity.activityTitle}", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun handleCommentClick(activity: PublishedActivity) {
        // TODO: Navigate to comments screen
        android.widget.Toast.makeText(this, "Comments for ${activity.activityTitle}", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun handleShareClick(activity: PublishedActivity) {
        // TODO: Implement share functionality
        android.widget.Toast.makeText(this, "Share ${activity.activityTitle}", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun handleBookmarkClick(activity: PublishedActivity) {
        // TODO: Implement bookmark functionality
        android.widget.Toast.makeText(this, "Bookmarked ${activity.activityTitle}", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun handleActivityClick(activity: PublishedActivity) {
        // Navigate to activity detail screen
        val intent = com.example.places.ui.detail.ActivityDetailActivity.newIntent(this, activity, "profile")
        startActivity(intent)
    }

    private fun handleEditClick(activity: PublishedActivity) {
        // Navigate to CreateActivityActivity with pre-filled data for editing
        val intent = com.example.places.ui.createactivity.CreateActivityActivity.newEditIntent(this, activity)
        startActivity(intent)
    }
}
