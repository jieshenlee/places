package com.example.places.ui.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.places.R
import com.example.places.databinding.ItemMessageBinding
import java.text.SimpleDateFormat
import java.util.*

data class MessageItem(
    val id: String,
    val senderId: String,
    val content: String,
    val timestamp: Date,
    val isRead: Boolean
)

class MessagesAdapter(
    private val currentUserId: String
) : ListAdapter<MessageItem, MessagesAdapter.MessageViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MessageViewHolder(
        private val binding: ItemMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: MessageItem) {
            val isOutgoing = message.senderId == currentUserId
            
            binding.apply {
                if (isOutgoing) {
                    // Show outgoing message
                    layoutOutgoingMessage.visibility = android.view.View.VISIBLE
                    layoutIncomingMessage.visibility = android.view.View.GONE
                    tvOutgoingMessage.text = message.content
                } else {
                    // Show incoming message
                    layoutIncomingMessage.visibility = android.view.View.VISIBLE
                    layoutOutgoingMessage.visibility = android.view.View.GONE
                    tvIncomingMessage.text = message.content
                    
                    // Load sender profile image
                    Glide.with(itemView.context)
                        .load(R.drawable.ic_person_placeholder)
                        .circleCrop()
                        .into(ivSenderProfile)
                }
                
                // Show timestamp for every 5th message or if it's been more than 10 minutes
                val shouldShowTimestamp = position % 5 == 0 || 
                    (position > 0 && isTimestampNeeded(message.timestamp, getItem(position - 1).timestamp))
                
                if (shouldShowTimestamp) {
                    tvTimestamp.visibility = android.view.View.VISIBLE
                    tvTimestamp.text = formatTimestamp(message.timestamp)
                } else {
                    tvTimestamp.visibility = android.view.View.GONE
                }
            }
        }
        
        private fun isTimestampNeeded(currentTime: Date, previousTime: Date): Boolean {
            val diffInMinutes = (currentTime.time - previousTime.time) / (1000 * 60)
            return diffInMinutes > 10
        }
        
        private fun formatTimestamp(date: Date): String {
            val now = Date()
            val diffInMillis = now.time - date.time
            val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)
            
            return when {
                diffInDays < 1 -> {
                    val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
                    formatter.format(date)
                }
                diffInDays < 7 -> {
                    val formatter = SimpleDateFormat("EEE h:mm a", Locale.getDefault())
                    formatter.format(date)
                }
                else -> {
                    val formatter = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
                    formatter.format(date)
                }
            }
        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<MessageItem>() {
        override fun areItemsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
            return oldItem == newItem
        }
    }
}
