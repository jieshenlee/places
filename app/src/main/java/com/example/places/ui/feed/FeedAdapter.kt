package com.example.places.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.places.R
import com.example.places.data.entity.FeedPost
import com.example.places.databinding.ItemFeedPostBinding
import java.text.SimpleDateFormat
import java.util.*

class FeedAdapter(
    private val onLikeClick: (FeedPost) -> Unit,
    private val onCommentClick: (FeedPost) -> Unit,
    private val onShareClick: (FeedPost) -> Unit,
    private val onBookmarkClick: (FeedPost) -> Unit,
    private val onUserClick: (FeedPost) -> Unit,
    private val onPostClick: (FeedPost) -> Unit
) : ListAdapter<FeedPost, FeedAdapter.FeedPostViewHolder>(FeedPostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedPostViewHolder {
        val binding = ItemFeedPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FeedPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedPostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FeedPostViewHolder(
        private val binding: ItemFeedPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: FeedPost) {
            with(binding) {
                // User info
                textViewUsername.text = post.username
                textViewLocation.text = post.location
                textViewDate.text = formatDate(post.createdAt)
                
                // Post content
                textViewDescription.text = post.description
                textViewTimeRange.text = post.timeRange
                
                // Interaction counts
                textViewLikeCount.text = post.likeCount.toString()
                textViewCommentCount.text = post.commentCount.toString()
                textViewShareCount.text = post.shareCount.toString()
                
                // Like state
                if (post.isLiked) {
                    imageViewLike.setImageResource(R.drawable.ic_favorite)
                    imageViewLike.setColorFilter(
                        itemView.context.getColor(R.color.md_theme_light_error)
                    )
                } else {
                    imageViewLike.setImageResource(R.drawable.ic_favorite_border)
                    imageViewLike.clearColorFilter()
                }
                
                // Bookmark state
                if (post.isBookmarked) {
                    buttonBookmark.setImageResource(R.drawable.ic_bookmark)
                    buttonBookmark.setColorFilter(
                        itemView.context.getColor(R.color.md_theme_light_primary)
                    )
                } else {
                    buttonBookmark.setImageResource(R.drawable.ic_bookmark_border)
                    buttonBookmark.clearColorFilter()
                }
                
                // Load profile image
                if (!post.userProfileImage.isNullOrEmpty()) {
                    Glide.with(itemView.context)
                        .load(post.userProfileImage)
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .into(imageViewProfile)
                } else {
                    imageViewProfile.setImageResource(R.drawable.ic_person)
                }
                
                // Load post image
                if (!post.imageUrl.isNullOrEmpty()) {
                    Glide.with(itemView.context)
                        .load(post.imageUrl)
                        .placeholder(R.drawable.ic_places_logo)
                        .error(R.drawable.ic_places_logo)
                        .into(imageViewPost)
                } else {
                    // Use sample images based on user
                    val sampleImage = when (post.username) {
                        "Sherwin Jieshen Li" -> R.drawable.ic_ocean_dock
                        "Ethan Ramirez" -> R.drawable.ic_mountain_landscape
                        "Priya Kapoor" -> R.drawable.ic_places_logo
                        else -> R.drawable.ic_ocean_dock
                    }
                    imageViewPost.setImageResource(sampleImage)
                }
                
                // Click listeners
                layoutLike.setOnClickListener { onLikeClick(post) }
                layoutComment.setOnClickListener { onCommentClick(post) }
                layoutShare.setOnClickListener { onShareClick(post) }
                buttonBookmark.setOnClickListener { onBookmarkClick(post) }
                
                imageViewProfile.setOnClickListener { onUserClick(post) }
                textViewUsername.setOnClickListener { onUserClick(post) }
                
                imageViewPost.setOnClickListener { onPostClick(post) }
                textViewDescription.setOnClickListener { onPostClick(post) }
            }
        }
        
        private fun formatDate(date: Date): String {
            val formatter = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
            return formatter.format(date)
        }
    }

    class FeedPostDiffCallback : DiffUtil.ItemCallback<FeedPost>() {
        override fun areItemsTheSame(oldItem: FeedPost, newItem: FeedPost): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FeedPost, newItem: FeedPost): Boolean {
            return oldItem == newItem
        }
    }
}
