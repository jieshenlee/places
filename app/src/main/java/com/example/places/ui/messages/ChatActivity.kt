package com.example.places.ui.messages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.places.R
import com.example.places.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var messagesAdapter: MessagesAdapter

    companion object {
        private const val EXTRA_CONVERSATION_ID = "extra_conversation_id"
        private const val EXTRA_USER_NAME = "extra_user_name"
        
        fun newIntent(context: Context, conversationId: String, userName: String): Intent {
            return Intent(context, ChatActivity::class.java).apply {
                putExtra(EXTRA_CONVERSATION_ID, conversationId)
                putExtra(EXTRA_USER_NAME, userName)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val conversationId = intent.getStringExtra(EXTRA_CONVERSATION_ID) ?: ""
        val userName = intent.getStringExtra(EXTRA_USER_NAME) ?: ""

        setupUI(userName)
        setupRecyclerView()
        setupClickListeners()
        loadSampleMessages()
    }

    private fun setupUI(userName: String) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        
        binding.tvUserName.text = userName
        
        // Load user profile image (placeholder for now)
        Glide.with(this)
            .load(R.drawable.ic_person_placeholder)
            .circleCrop()
            .into(binding.ivUserProfile)
    }

    private fun setupRecyclerView() {
        messagesAdapter = MessagesAdapter("current_user_id")
        
        binding.rvMessages.apply {
            adapter = messagesAdapter
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabSend.setOnClickListener {
            sendMessage()
        }
        
        binding.ivAttach.setOnClickListener {
            // TODO: Handle attachment
        }
        
        binding.ivMoreOptions.setOnClickListener {
            // TODO: Show options menu
        }
    }

    private fun sendMessage() {
        val messageText = binding.etMessage.text?.toString()?.trim()
        if (!messageText.isNullOrEmpty()) {
            // TODO: Send message to repository
            binding.etMessage.text?.clear()
        }
    }

    private fun loadSampleMessages() {
        val sampleMessages = listOf(
            MessageItem("1", "other_user", "Hey! How was your trip to Bali?", java.util.Date(), false),
            MessageItem("2", "current_user_id", "It was amazing! The beaches were incredible.", java.util.Date(), true),
            MessageItem("3", "other_user", "I'm so jealous! I've been wanting to go there for ages.", java.util.Date(), false),
            MessageItem("4", "current_user_id", "You should definitely go! I can share some recommendations.", java.util.Date(), true),
            MessageItem("5", "other_user", "That would be awesome! Thanks!", java.util.Date(), false)
        )
        
        messagesAdapter.submitList(sampleMessages)
    }
}
