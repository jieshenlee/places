package com.example.places.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.places.R
import com.example.places.data.entity.TravelCard
import java.text.SimpleDateFormat
import java.util.*

class TravelCardAdapter(
    private val onItemClick: (TravelCard) -> Unit,
    private var isGridView: Boolean = true
) : ListAdapter<TravelCard, RecyclerView.ViewHolder>(TravelCardDiffCallback()) {

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
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_travel_card_grid, parent, false)
                GridViewHolder(view)
            }
            VIEW_TYPE_LIST -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_travel_card_list, parent, false)
                ListViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val travelCard = getItem(position)
        when (holder) {
            is GridViewHolder -> holder.bind(travelCard)
            is ListViewHolder -> holder.bind(travelCard)
        }
    }

    fun setViewType(isGrid: Boolean) {
        if (this.isGridView != isGrid) {
            this.isGridView = isGrid
            notifyDataSetChanged()
        }
    }

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.iv_travel_image)
        private val locationText: TextView = itemView.findViewById(R.id.tv_location)

        fun bind(travelCard: TravelCard) {
            locationText.text = travelCard.location

            // Load first image if available
            if (travelCard.imageUrls.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(travelCard.imageUrls.first())
                    .placeholder(R.drawable.ic_mountain_landscape)
                    .error(R.drawable.ic_mountain_landscape)
                    .centerCrop()
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.ic_mountain_landscape)
            }

            itemView.setOnClickListener {
                onItemClick(travelCard)
            }
        }
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userProfileImage: ImageView = itemView.findViewById(R.id.iv_user_profile)
        private val userName: TextView = itemView.findViewById(R.id.tv_user_name)
        private val location: TextView = itemView.findViewById(R.id.tv_location)
        private val date: TextView = itemView.findViewById(R.id.tv_date)
        private val description: TextView = itemView.findViewById(R.id.tv_description)
        private val time: TextView = itemView.findViewById(R.id.tv_time)
        private val travelImage: ImageView = itemView.findViewById(R.id.iv_travel_image)
        private val likesCount: TextView = itemView.findViewById(R.id.tv_likes_count)
        private val commentsCount: TextView = itemView.findViewById(R.id.tv_comments_count)
        private val sharesCount: TextView = itemView.findViewById(R.id.tv_shares_count)

        fun bind(travelCard: TravelCard) {
            // Set travel card data
            userName.text = "Sherwin Jieshen Li" // TODO: Get from user data
            location.text = travelCard.location
            description.text = travelCard.description
            
            // Format date
            val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
            date.text = dateFormat.format(travelCard.createdAt)
            
            // Set placeholder time - TODO: Add time field to TravelCard entity
            time.text = "1:00pm to 2:00pm"
            
            // Set counts
            likesCount.text = travelCard.likesCount.toString()
            commentsCount.text = travelCard.commentsCount.toString()
            sharesCount.text = "5" // TODO: Add shares count to entity

            // Load travel image
            if (travelCard.imageUrls.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(travelCard.imageUrls.first())
                    .placeholder(R.drawable.ic_mountain_landscape)
                    .error(R.drawable.ic_mountain_landscape)
                    .centerCrop()
                    .into(travelImage)
            } else {
                travelImage.setImageResource(R.drawable.ic_mountain_landscape)
            }

            // Load user profile image - TODO: Get from user repository
            userProfileImage.setImageResource(R.drawable.ic_person_placeholder)

            itemView.setOnClickListener {
                onItemClick(travelCard)
            }
        }
    }

    class TravelCardDiffCallback : DiffUtil.ItemCallback<TravelCard>() {
        override fun areItemsTheSame(oldItem: TravelCard, newItem: TravelCard): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TravelCard, newItem: TravelCard): Boolean {
            return oldItem == newItem
        }
    }
}
