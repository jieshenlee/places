package com.example.places.ui.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.places.R
import com.example.places.data.entity.ExplorePerson
import com.example.places.databinding.ItemExplorePersonBinding

class ExplorePersonAdapter(
    private val onPersonClick: (ExplorePerson) -> Unit,
    private val onFollowClick: (ExplorePerson) -> Unit
) : ListAdapter<ExplorePerson, ExplorePersonAdapter.PersonViewHolder>(PersonDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val binding = ItemExplorePersonBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PersonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PersonViewHolder(
        private val binding: ItemExplorePersonBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(person: ExplorePerson) {
            binding.apply {
                tvPersonName.text = person.name
                tvPersonBio.text = person.bio
                
                // Format followers count
                tvFollowersCount.text = formatCount(person.followersCount) + " followers"
                tvTripsCount.text = "${person.tripsCount} trips"

                // Load profile image
                person.profileImageUrl?.let { imageUrl ->
                    Glide.with(itemView.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_person_placeholder)
                        .error(R.drawable.ic_person_placeholder)
                        .circleCrop()
                        .into(ivPersonProfile)
                } ?: run {
                    ivPersonProfile.setImageResource(R.drawable.ic_person_placeholder)
                }

                // Handle follow button state
                if (person.isFollowing) {
                    btnFollow.text = "Following"
                    btnFollow.setBackgroundColor(itemView.context.getColor(R.color.md_theme_light_primaryContainer))
                } else {
                    btnFollow.text = "Follow"
                    btnFollow.setBackgroundColor(itemView.context.getColor(R.color.md_theme_light_surface))
                }

                // Set click listeners
                root.setOnClickListener {
                    onPersonClick(person)
                }

                btnFollow.setOnClickListener {
                    onFollowClick(person)
                }
            }
        }

        private fun formatCount(count: Int): String {
            return when {
                count >= 1000000 -> "${count / 1000000}.${(count % 1000000) / 100000}M"
                count >= 1000 -> "${count / 1000}.${(count % 1000) / 100}k"
                else -> count.toString()
            }
        }
    }

    class PersonDiffCallback : DiffUtil.ItemCallback<ExplorePerson>() {
        override fun areItemsTheSame(oldItem: ExplorePerson, newItem: ExplorePerson): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExplorePerson, newItem: ExplorePerson): Boolean {
            return oldItem == newItem
        }
    }
}
