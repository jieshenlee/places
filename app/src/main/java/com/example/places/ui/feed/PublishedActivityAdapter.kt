package com.example.places.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.places.R
import com.example.places.databinding.ItemPublishedActivityBinding
import com.example.places.databinding.ItemPublishedActivityGridBinding
import com.example.places.data.entity.PublishedActivity

class PublishedActivityAdapter(
    private val onLikeClick: (PublishedActivity) -> Unit,
    private val onCommentClick: (PublishedActivity) -> Unit,
    private val onShareClick: (PublishedActivity) -> Unit,
    private val onBookmarkClick: (PublishedActivity) -> Unit,
    private val onActivityClick: (PublishedActivity) -> Unit,
    private val onEditClick: (PublishedActivity) -> Unit,
    private var isGridView: Boolean = false
) : ListAdapter<PublishedActivity, RecyclerView.ViewHolder>(PublishedActivityDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_GRID = 0
        private const val VIEW_TYPE_LIST = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (isGridView) VIEW_TYPE_GRID else VIEW_TYPE_LIST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_GRID -> {
                val binding = ItemPublishedActivityGridBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                GridViewHolder(binding)
            }
            VIEW_TYPE_LIST -> {
                val binding = ItemPublishedActivityBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ListViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val activity = getItem(position)
        when (holder) {
            is GridViewHolder -> holder.bind(activity)
            is ListViewHolder -> holder.bind(activity)
        }
    }

    fun setViewType(isGrid: Boolean) {
        if (this.isGridView != isGrid) {
            this.isGridView = isGrid
            notifyDataSetChanged()
        }
    }

    inner class GridViewHolder(
        private val binding: ItemPublishedActivityGridBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(activity: PublishedActivity) {
            // Load activity image using Glide (only image in grid view)
            activity.activityImage?.let { imageUrl ->
                Glide.with(binding.ivActivityImage.context)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(binding.ivActivityImage)
            }

            // Set click listener for the entire card
            binding.root.setOnClickListener { onActivityClick(activity) }
        }
    }

    inner class ListViewHolder(
        private val binding: ItemPublishedActivityBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(activity: PublishedActivity) {
            binding.tvLocation.text = activity.location

            // Load hero image using Glide
            activity.heroImage?.let { imageUrl ->
                Glide.with(binding.ivHeroImage.context)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(binding.ivHeroImage)
            }

            // Set click listeners
            binding.root.setOnClickListener { onActivityClick(activity) }
            binding.ivEdit.setOnClickListener { onEditClick(activity) }
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
