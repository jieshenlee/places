package com.example.places.ui.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.places.R
import com.example.places.databinding.ItemConversationBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class ConversationItem(
    val id: String,
    val userName: String,
    val userProfileImage: String?,
    val lastMessage: String,
    val timestamp: Date,
    val isUnread: Boolean
)

class ConversationsAdapter(
    private val onConversationClick: (ConversationItem) -> Unit
) : ListAdapter<ConversationItem, ConversationsAdapter.ConversationViewHolder>(ConversationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val binding = ItemConversationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ConversationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ConversationViewHolder(
        private val binding: ItemConversationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(conversation: ConversationItem) {
            binding.apply {
                tvUserName.text = conversation.userName
                tvLastMessage.text = conversation.lastMessage
                tvTimestamp.text = getTimeAgo(conversation.timestamp)

                // Load profile image
                conversation.userProfileImage?.let { imageUrl ->
                    Glide.with(itemView.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_person_placeholder)
                        .error(R.drawable.ic_person_placeholder)
                        .circleCrop()
                        .into(ivUserProfile)
                } ?: run {
                    ivUserProfile.setImageResource(R.drawable.ic_person_placeholder)
                }

                // Show/hide unread indicator
                if (conversation.isUnread) {
                    unreadIndicator.visibility = android.view.View.VISIBLE
                    tvLastMessage.setTextColor(itemView.context.getColor(R.color.md_theme_light_onSurface))
                    tvUserName.setTextColor(itemView.context.getColor(R.color.md_theme_light_onSurface))
                } else {
                    unreadIndicator.visibility = android.view.View.GONE
                    tvLastMessage.setTextColor(itemView.context.getColor(R.color.md_theme_light_onSurfaceVariant))
                    tvUserName.setTextColor(itemView.context.getColor(R.color.md_theme_light_onSurface))
                }

                // Set click listener
                root.setOnClickListener {
                    onConversationClick(conversation)
                }
            }
        }

        private fun getTimeAgo(date: Date): String {
            val now = Date()
            val diffInMillis = now.time - date.time
            
            return when {
                diffInMillis < TimeUnit.MINUTES.toMillis(1) -> "now"
                diffInMillis < TimeUnit.HOURS.toMillis(1) -> {
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
                    "${minutes}m"
                }
                diffInMillis < TimeUnit.DAYS.toMillis(1) -> {
                    val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
                    "${hours}h"
                }
                diffInMillis < TimeUnit.DAYS.toMillis(7) -> {
                    val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
                    "${days}d"
                }
                else -> {
                    val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
                    formatter.format(date)
                }
            }
        }
    }

    class ConversationDiffCallback : DiffUtil.ItemCallback<ConversationItem>() {
        override fun areItemsTheSame(oldItem: ConversationItem, newItem: ConversationItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ConversationItem, newItem: ConversationItem): Boolean {
            return oldItem == newItem
        }
    }
}
