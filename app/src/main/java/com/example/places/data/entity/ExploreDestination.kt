package com.example.places.data.entity

data class ExploreDestination(
    val id: String,
    val name: String,
    val location: String,
    val country: String,
    val imageUrl: String,
    val description: String? = null,
    val category: String = "destination", // "destination", "city", "landmark"
    val isBookmarked: Boolean = false,
    val popularityScore: Int = 0,
    val tags: List<String> = emptyList()
)

data class ExplorePerson(
    val id: String,
    val name: String,
    val username: String,
    val bio: String,
    val profileImageUrl: String? = null,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val tripsCount: Int = 0,
    val isFollowing: Boolean = false,
    val isVerified: Boolean = false
)
