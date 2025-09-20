package com.example.places.ui.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.places.R
import com.example.places.data.entity.ExploreDestination
import com.example.places.databinding.ItemExploreDestinationBinding

class ExploreDestinationAdapter(
    private val onDestinationClick: (ExploreDestination) -> Unit,
    private val onBookmarkClick: (ExploreDestination) -> Unit
) : ListAdapter<ExploreDestination, ExploreDestinationAdapter.DestinationViewHolder>(DestinationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder {
        val binding = ItemExploreDestinationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DestinationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DestinationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DestinationViewHolder(
        private val binding: ItemExploreDestinationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(destination: ExploreDestination) {
            binding.apply {
                tvDestinationName.text = destination.name
                tvDestinationLocation.text = destination.location

                // Load destination image
                Glide.with(itemView.context)
                    .load(destination.imageUrl)
                    .placeholder(R.drawable.ic_mountain_landscape)
                    .error(R.drawable.ic_mountain_landscape)
                    .centerCrop()
                    .into(ivDestination)

                // Handle bookmark state
                if (destination.isBookmarked) {
                    ivBookmark.setImageResource(R.drawable.ic_bookmark)
                    ivBookmark.visibility = android.view.View.VISIBLE
                } else {
                    ivBookmark.setImageResource(R.drawable.ic_bookmark_border)
                    ivBookmark.visibility = android.view.View.GONE
                }

                // Set click listeners
                root.setOnClickListener {
                    onDestinationClick(destination)
                }

                ivBookmark.setOnClickListener {
                    onBookmarkClick(destination)
                }
            }
        }
    }

    class DestinationDiffCallback : DiffUtil.ItemCallback<ExploreDestination>() {
        override fun areItemsTheSame(oldItem: ExploreDestination, newItem: ExploreDestination): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExploreDestination, newItem: ExploreDestination): Boolean {
            return oldItem == newItem
        }
    }
}
