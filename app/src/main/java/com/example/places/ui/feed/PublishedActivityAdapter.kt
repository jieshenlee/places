package com.example.places.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.places.R
import com.example.places.databinding.ItemPublishedActivityBinding
import com.example.places.data.entity.PublishedActivity

class PublishedActivityAdapter(
    private val onLikeClick: (PublishedActivity) -> Unit,
    private val onCommentClick: (PublishedActivity) -> Unit,
    private val onShareClick: (PublishedActivity) -> Unit,
    private val onBookmarkClick: (PublishedActivity) -> Unit,
    private val onActivityClick: (PublishedActivity) -> Unit,
    private val onEditClick: (PublishedActivity) -> Unit
) : ListAdapter<PublishedActivity, PublishedActivityAdapter.ViewHolder>(PublishedActivityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPublishedActivityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemPublishedActivityBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(activity: PublishedActivity) {
            binding.tvUsername.text = activity.username
            binding.tvLocation.text = activity.location
            binding.tvDate.text = activity.date
            binding.tvDescription.text = activity.description
            binding.tvActivityTitle.text = activity.activityTitle
            binding.tvActivityDescription.text = activity.activityDescription
            binding.tvActivityTime.text = activity.activityTime
            binding.tvLikeCount.text = activity.likeCount.toString()
            binding.tvCommentCount.text = activity.commentCount.toString()
            binding.tvShareCount.text = activity.shareCount.toString()

            // Load images using Glide
            activity.userProfileImage?.let { imageUrl ->
                Glide.with(binding.ivUserProfile.context)
                    .load(imageUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(binding.ivUserProfile)
            }

            activity.heroImage?.let { imageUrl ->
                Glide.with(binding.ivHeroImage.context)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(binding.ivHeroImage)
            }

            activity.activityImage?.let { imageUrl ->
                Glide.with(binding.ivActivityImage.context)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(binding.ivActivityImage)
            }

            // Update like state
            binding.ivLike.setImageResource(
                if (activity.isLiked) R.drawable.ic_favorite 
                else R.drawable.ic_favorite_border
            )

            // Update bookmark state
            binding.ivBookmark.setImageResource(
                if (activity.isBookmarked) R.drawable.ic_bookmark 
                else R.drawable.ic_bookmark_border
            )

            // Set click listeners
            binding.layoutLike.setOnClickListener { onLikeClick(activity) }
            binding.layoutComment.setOnClickListener { onCommentClick(activity) }
            binding.layoutShare.setOnClickListener { onShareClick(activity) }
            binding.ivBookmark.setOnClickListener { onBookmarkClick(activity) }
            binding.ivEdit.setOnClickListener { onEditClick(activity) }
            binding.cardActivity.setOnClickListener { onActivityClick(activity) }
        }
    }
}

class PublishedActivityDiffCallback : DiffUtil.ItemCallback<PublishedActivity>() {
    override fun areItemsTheSame(oldItem: PublishedActivity, newItem: PublishedActivity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PublishedActivity, newItem: PublishedActivity): Boolean {
        return oldItem == newItem
    }
}
