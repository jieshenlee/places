package com.example.places.ui.createactivity

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.places.R
import com.example.places.databinding.ActivityCreateActivityBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CreateActivityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateActivityBinding
    private val calendar = Calendar.getInstance()
    private var selectedImageUri: Uri? = null
    private var tempCameraImageUri: Uri? = null

    // Activity result launchers for image selection
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            updateImagePreview()
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            selectedImageUri = tempCameraImageUri
            updateImagePreview()
        }
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val cameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: false
        val storagePermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: false
        } else {
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
        }
        
        if (cameraPermissionGranted && storagePermissionGranted) {
            showImagePickerDialog()
        } else {
            Toast.makeText(this, "Permissions required for image selection", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_LOCATION = "LOCATION"
        const val EXTRA_NOTES = "NOTES"
        const val EXTRA_DATE = "DATE"
        const val EXTRA_IMAGE_URI = "IMAGE_URI"
        const val EXTRA_EDIT_MODE = "EDIT_MODE"
        const val EXTRA_ACTIVITY_DATA = "ACTIVITY_DATA"
        
        fun newIntent(context: Context): Intent {
            return Intent(context, CreateActivityActivity::class.java)
        }
        
        fun newEditIntent(context: Context, activity: com.example.places.data.entity.PublishedActivity): Intent {
            return Intent(context, CreateActivityActivity::class.java).apply {
                putExtra(EXTRA_EDIT_MODE, true)
                putExtra(EXTRA_ACTIVITY_DATA, activity)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupClickListeners()
        setupBottomNavigation()
        populateFieldsFromIntent()
    }

    private fun populateFieldsFromIntent() {
        if (intent.getBooleanExtra(EXTRA_EDIT_MODE, false)) {
            // Handle edit mode - populate with existing activity data
            val activityData = intent.getSerializableExtra(EXTRA_ACTIVITY_DATA) as? com.example.places.data.entity.PublishedActivity
            activityData?.let { activity ->
                binding.etLocation.setText(activity.location)
                binding.etNotes.setText(activity.description)
                binding.etDate.setText(activity.date)
                binding.etActivityName.setText(activity.activityTitle)
                binding.etDescription.setText(activity.activityDescription)
                binding.etTime.setText(activity.activityTime)
                
                // Load existing images
                activity.heroImage?.let { imageUrl ->
                    selectedImageUri = Uri.parse(imageUrl)
                    displaySelectedImage()
                }
                
                // Update toolbar title to indicate edit mode
                supportActionBar?.title = "Edit Activity"
            }
        } else {
            // Handle create mode - populate with data from AddActivityActivity
            val location = intent.getStringExtra(EXTRA_LOCATION)
            val notes = intent.getStringExtra(EXTRA_NOTES)
            val date = intent.getStringExtra(EXTRA_DATE)
            val imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)

            // Populate the first three fields with the received data
            location?.let { binding.etLocation.setText(it) }
            notes?.let { binding.etNotes.setText(it) }
            date?.let { binding.etDate.setText(it) }
            
            // Handle image URI
            imageUriString?.let { uriString ->
                selectedImageUri = Uri.parse(uriString)
                displaySelectedImage()
            }
        }
    }
    
    private fun displaySelectedImage() {
        selectedImageUri?.let { uri ->
            Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(binding.ivMediaPreview)
        }
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        binding.etTime.setOnClickListener {
            showTimePicker()
        }

        binding.btnBrowseFiles.setOnClickListener {
            checkPermissionsAndShowImagePicker()
        }

        binding.btnSaveActivity.setOnClickListener {
            saveActivity()
        }

        binding.btnAddActivity.setOnClickListener {
            addNewActivity()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_feed -> {
                    startActivity(Intent(this, com.example.places.ui.feed.FeedActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_messages -> {
                    startActivity(Intent(this, com.example.places.ui.messages.MessagesActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_explore -> {
                    startActivity(Intent(this, com.example.places.ui.explore.ExploreActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_notifications -> {
                    startActivity(Intent(this, com.example.places.ui.notifications.NotificationsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, com.example.places.ui.profile.ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateField()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                updateTimeField()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private fun updateDateField() {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        binding.etDate.setText(dateFormat.format(calendar.time))
    }

    private fun updateTimeField() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        binding.etTime.setText(timeFormat.format(calendar.time))
    }

    private fun saveActivity() {
        val location = binding.etLocation.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val activityName = binding.etActivityName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val time = binding.etTime.text.toString().trim()

        if (location.isEmpty() || activityName.isEmpty()) {
            Toast.makeText(this, "Please fill in required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Navigate to preview screen with the entered data
        val activityDuration = if (time.isNotEmpty()) "$time-${getEndTime(time)}" else "1.00pm-2.30pm"
        val intent = com.example.places.ui.entrypreview.EntryPreviewActivity.newIntent(
            this,
            location,
            date.ifEmpty { "21st Saturday November 2025" },
            notes.ifEmpty { "Lorem ipsum dolor sit amet consectetur. Posuere tincidunt nec ipsum tincidunt nisl a. Urna viverra turpis mauris dictumst augue." },
            activityName,
            description.ifEmpty { "Lorem ipsum dolor sit amet consectetur. Porta id facilisis lorem id porttitor." },
            "Today, ${time.ifEmpty { "9.00AM" }}",
            activityDuration
        )
        startActivity(intent)
        finish()
    }
    
    private fun getEndTime(startTime: String): String {
        // Simple logic to add 1.5 hours to start time
        // In a real app, you'd use proper time parsing
        return when {
            startTime.contains("9.00AM") -> "10.30AM"
            startTime.contains("10.00AM") -> "11.30AM"
            startTime.contains("11.00AM") -> "12.30PM"
            startTime.contains("12.00PM") -> "1.30PM"
            startTime.contains("1.00PM") -> "2.30PM"
            startTime.contains("2.00PM") -> "3.30PM"
            else -> "2.30PM"
        }
    }

    private fun addNewActivity() {
        // TODO: Add logic to create additional activity cards dynamically
        Toast.makeText(this, "Add new activity functionality coming soon", Toast.LENGTH_SHORT).show()
    }
    
    private fun checkPermissionsAndShowImagePicker() {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        
        if (cameraPermission == PackageManager.PERMISSION_GRANTED && 
            storagePermission == PackageManager.PERMISSION_GRANTED) {
            showImagePickerDialog()
        } else {
            val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            permissionLauncher.launch(permissions)
        }
    }
    
    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        AlertDialog.Builder(this)
            .setTitle("Select Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }
    
    private fun openCamera() {
        val imageFile = File(externalCacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        tempCameraImageUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            imageFile
        )
        tempCameraImageUri?.let { uri ->
            cameraLauncher.launch(uri)
        }
    }
    
    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }
    
    private fun updateImagePreview() {
        selectedImageUri?.let { uri ->
            Glide.with(this)
                .load(uri)
                .into(binding.ivMediaPreview)
            
            // Update button text to show image is selected
            binding.btnBrowseFiles.text = "✓ Image Selected"
        }
    }
}
