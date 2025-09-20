package com.example.places.ui.messages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.places.R
import com.example.places.databinding.ActivityMessagesBinding
import java.util.*

class MessagesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessagesBinding
    private lateinit var conversationsAdapter: ConversationsAdapter

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MessagesActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearch()
        setupClickListeners()
        loadSampleData()
    }

    private fun setupRecyclerView() {
        conversationsAdapter = ConversationsAdapter { conversation ->
            // TODO: Navigate to chat conversation
            val intent = ChatActivity.newIntent(this, conversation.id, conversation.userName)
            startActivity(intent)
        }

        binding.rvConversations.apply {
            adapter = conversationsAdapter
            layoutManager = LinearLayoutManager(this@MessagesActivity)
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString() ?: ""
                filterConversations(query)
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupClickListeners() {
        binding.fabNewMessage.setOnClickListener {
            startActivity(Intent(this, com.example.places.ui.addactivity.AddActivityActivity::class.java))
        }
        
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_messages
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_feed -> {
                    startActivity(Intent(this, com.example.places.ui.feed.FeedActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_messages -> {
                    // Already on messages page
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

    private fun filterConversations(query: String) {
        // TODO: Implement search filtering
    }

    private fun loadSampleData() {
        val now = Date()
        val sampleConversations = listOf(
            ConversationItem(
                id = "1",
                userName = "Sophia Clark",
                userProfileImage = null,
                lastMessage = "See you soon!",
                timestamp = Date(now.time - (2 * 60 * 1000)), // 2 minutes ago
                isUnread = true
            ),
            ConversationItem(
                id = "2",
                userName = "Ethan Carter",
                userProfileImage = null,
                lastMessage = "I'm in the city",
                timestamp = Date(now.time - (15 * 60 * 1000)), // 15 minutes ago
                isUnread = false
            ),
            ConversationItem(
                id = "3",
                userName = "Olivia Bennett",
                userProfileImage = null,
                lastMessage = "I'm in the city",
                timestamp = Date(now.time - (1 * 60 * 60 * 1000)), // 1 hour ago
                isUnread = false
            ),
            ConversationItem(
                id = "4",
                userName = "Liam Harper",
                userProfileImage = null,
                lastMessage = "I'm in the city",
                timestamp = Date(now.time - (3 * 60 * 60 * 1000)), // 3 hours ago
                isUnread = false
            ),
            ConversationItem(
                id = "5",
                userName = "Ava Foster",
                userProfileImage = null,
                lastMessage = "I'm in the city",
                timestamp = Date(now.time - (6 * 60 * 60 * 1000)), // 6 hours ago
                isUnread = false
            ),
            ConversationItem(
                id = "6",
                userName = "Noah Reed",
                userProfileImage = null,
                lastMessage = "I'm in the city",
                timestamp = Date(now.time - (12 * 60 * 60 * 1000)), // 12 hours ago
                isUnread = false
            )
        )

        conversationsAdapter.submitList(sampleConversations)
        
        // Show/hide empty state
        if (sampleConversations.isEmpty()) {
            binding.layoutEmptyState.visibility = android.view.View.VISIBLE
            binding.rvConversations.visibility = android.view.View.GONE
        } else {
            binding.layoutEmptyState.visibility = android.view.View.GONE
            binding.rvConversations.visibility = android.view.View.VISIBLE
        }
    }
}
