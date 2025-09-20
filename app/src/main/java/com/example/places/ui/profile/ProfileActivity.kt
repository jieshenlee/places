package com.example.places.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.places.PlacesApplication
import com.example.places.R
import com.example.places.databinding.ActivityProfileBinding
import com.google.android.material.tabs.TabLayout

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var travelCardAdapter: TravelCardAdapter
    
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

        setupUI()
        setupRecyclerView()
        observeViewModel()
        setupClickListeners()
        setupBottomNavigation()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        
        // Set initial view mode to grid
        updateViewToggleButtons(isGridView = true)
        
        // Set default tab selection
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

        viewModel.isLoading.observe(this) { isLoading ->
            // TODO: Show/hide loading indicator
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
                // TODO: Navigate to edit profile activity
            }
            
            btnGridView.setOnClickListener {
                updateViewToggleButtons(isGridView = true)
                updateRecyclerViewLayout(isGridView = true)
                travelCardAdapter.setViewType(isGrid = true)
            }
            
            btnListView.setOnClickListener {
                updateViewToggleButtons(isGridView = false)
                updateRecyclerViewLayout(isGridView = false)
                travelCardAdapter.setViewType(isGrid = false)
            }
            
            fabAddTrip.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, com.example.places.ui.addactivity.AddActivityActivity::class.java))
            }
            
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> {
                            // Show user's trips
                            viewModel.userTravelCards.value?.let { cards ->
                                travelCardAdapter.submitList(cards)
                            }
                        }
                        1 -> {
                            // Show saved trips (TODO: implement saved functionality)
                            travelCardAdapter.submitList(emptyList())
                        }
                    }
                }
                
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
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
}
