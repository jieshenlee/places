package com.example.places.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.places.PlacesApplication
import com.example.places.R
import com.example.places.databinding.ActivityTravelCardDetailBinding
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.util.*

class TravelCardDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTravelCardDetailBinding
    private lateinit var imageCarouselAdapter: ImageCarouselAdapter
    
    private val viewModel: TravelCardDetailViewModel by viewModels {
        TravelCardDetailViewModelFactory(
            (application as PlacesApplication).travelCardRepository,
            (application as PlacesApplication).userRepository
        )
    }

    companion object {
        private const val EXTRA_TRAVEL_CARD_ID = "extra_travel_card_id"
        
        fun newIntent(context: Context, travelCardId: String): Intent {
            return Intent(context, TravelCardDetailActivity::class.java).apply {
                putExtra(EXTRA_TRAVEL_CARD_ID, travelCardId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTravelCardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val travelCardId = intent.getStringExtra(EXTRA_TRAVEL_CARD_ID)
        if (travelCardId == null) {
            finish()
            return
        }

        setupUI()
        observeViewModel()
        setupClickListeners()
        
        viewModel.loadTravelCard(travelCardId)
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.travelCard.observe(this) { travelCard ->
            travelCard?.let { updateTravelCardInfo(it) }
        }

        viewModel.cardOwner.observe(this) { user ->
            user?.let { updateUserInfo(it) }
        }

        viewModel.isLiked.observe(this) { isLiked ->
            updateLikeButton(isLiked)
        }

        viewModel.isSaved.observe(this) { isSaved ->
            updateSaveButton(isSaved)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            // TODO: Show/hide loading indicator
        }
    }

    private fun updateTravelCardInfo(travelCard: com.example.places.data.entity.TravelCard) {
        binding.apply {
            tvLocation.text = travelCard.location
            tvDescription.text = travelCard.description
            
            // Format date
            val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
            tvDate.text = dateFormat.format(travelCard.createdAt)
            
            // Setup image carousel
            setupImageCarousel(travelCard.imageUrls)
        }
    }

    private fun updateUserInfo(user: com.example.places.data.entity.User) {
        binding.apply {
            tvUserName.text = user.displayName
            
            // Load profile image
            user.profileImageUrl?.let { imageUrl ->
                Glide.with(this@TravelCardDetailActivity)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_person_placeholder)
                    .error(R.drawable.ic_person_placeholder)
                    .circleCrop()
                    .into(ivUserProfile)
            }
        }
    }

    private fun setupImageCarousel(imageUrls: List<String>) {
        val images = if (imageUrls.isNotEmpty()) imageUrls else listOf("placeholder")
        
        imageCarouselAdapter = ImageCarouselAdapter(images)
        binding.vpImages.adapter = imageCarouselAdapter
        
        // Setup indicators
        TabLayoutMediator(binding.tabIndicators, binding.vpImages) { _, _ ->
            // Empty implementation - just creates dots
        }.attach()
    }

    private fun setupClickListeners() {
        binding.apply {
            btnLike1.setOnClickListener {
                viewModel.toggleLike()
            }
            
            btnBookmark1.setOnClickListener {
                viewModel.toggleSave()
            }
            
            btnShare1.setOnClickListener {
                viewModel.shareCard()
            }
            
            btnShareExternal.setOnClickListener {
                viewModel.shareCard()
            }
            
            btnComment1.setOnClickListener {
                // TODO: Navigate to comments screen
            }
        }
    }

    private fun updateLikeButton(isLiked: Boolean) {
        val iconRes = if (isLiked) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        val colorRes = if (isLiked) R.color.md_theme_light_error else R.color.md_theme_light_onSurfaceVariant
        
        binding.btnLike1.setIconResource(iconRes)
        binding.btnLike1.setIconTintResource(colorRes)
    }

    private fun updateSaveButton(isSaved: Boolean) {
        val iconRes = if (isSaved) R.drawable.ic_bookmark else R.drawable.ic_bookmark_border
        val colorRes = if (isSaved) R.color.md_theme_light_primary else R.color.md_theme_light_onSurfaceVariant
        
        binding.btnBookmark1.setIconResource(iconRes)
        binding.btnBookmark1.setIconTintResource(colorRes)
    }
}
