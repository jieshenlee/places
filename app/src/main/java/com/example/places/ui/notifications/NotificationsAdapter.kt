package com.example.places.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.places.R
import com.example.places.data.entity.Notification
import com.example.places.data.entity.NotificationType
import com.example.places.databinding.ItemNotificationBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationsAdapter(
    private val onNotificationClick: (Notification) -> Unit
) : ListAdapter<Notification, NotificationsAdapter.NotificationViewHolder>(NotificationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NotificationViewHolder(
        private val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: Notification) {
            binding.apply {
                tvNotificationText.text = notification.message
                tvTimeStamp.text = getTimeAgo(notification.createdAt)

                // Load user profile image
                notification.imageUrl?.let { imageUrl ->
                    Glide.with(itemView.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_person_placeholder)
                        .error(R.drawable.ic_person_placeholder)
                        .circleCrop()
                        .into(ivUserProfile)
                } ?: run {
                    ivUserProfile.setImageResource(R.drawable.ic_person_placeholder)
                }

                // Set notification type icon
                when (notification.type) {
                    NotificationType.LIKE -> {
                        ivNotificationIcon.setImageResource(R.drawable.ic_favorite)
                        ivNotificationIcon.visibility = android.view.View.VISIBLE
                    }
                    NotificationType.COMMENT -> {
                        ivNotificationIcon.setImageResource(R.drawable.ic_comment)
                        ivNotificationIcon.visibility = android.view.View.VISIBLE
                    }
                    NotificationType.FOLLOW -> {
                        ivNotificationIcon.setImageResource(R.drawable.ic_person_add)
                        ivNotificationIcon.visibility = android.view.View.VISIBLE
                    }
                    else -> {
                        ivNotificationIcon.visibility = android.view.View.GONE
                    }
                }

                // Handle post thumbnail for post-related notifications
                if (notification.type == NotificationType.LIKE || 
                    notification.type == NotificationType.COMMENT) {
                    // TODO: Load travel card thumbnail
                    ivPostThumbnail.visibility = android.view.View.VISIBLE
                    ivPostThumbnail.setImageResource(R.drawable.ic_mountain_landscape)
                } else {
                    ivPostThumbnail.visibility = android.view.View.GONE
                }

                // Set click listener
                root.setOnClickListener {
                    onNotificationClick(notification)
                }

                // Visual indicator for unread notifications
                if (!notification.isRead) {
                    root.alpha = 1.0f
                    root.setBackgroundColor(itemView.context.getColor(R.color.md_theme_light_primaryContainer))
                } else {
                    root.alpha = 0.8f
                    root.background = null
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

    class NotificationDiffCallback : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }
    }
}
