package com.example.places.ui.explore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.places.R
import com.example.places.databinding.ActivityExploreBinding
import com.google.android.material.tabs.TabLayoutMediator

class ExploreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExploreBinding
    private lateinit var pagerAdapter: ExplorePagerAdapter

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ExploreActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExploreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupSearch()
        setupClickListeners()
    }

    private fun setupViewPager() {
        pagerAdapter = ExplorePagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        // Connect TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Trips"
                1 -> "People"
                2 -> "Places"
                else -> "Trips"
            }
        }.attach()
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO: Implement search functionality
                val query = s?.toString() ?: ""
                performSearch(query)
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupClickListeners() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, com.example.places.ui.addactivity.AddActivityActivity::class.java))
        }
        
        setupBottomNavigation()
    }

    private fun performSearch(query: String) {
        // TODO: Implement search across all tabs
        // For now, this is a placeholder
        if (query.isNotEmpty()) {
            // Filter current tab content based on search query
        }
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_explore
        
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
                    // Already on explore page
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
}
