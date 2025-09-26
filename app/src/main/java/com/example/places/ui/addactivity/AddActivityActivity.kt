package com.example.places.ui.addactivity

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.places.PlacesApplication
import com.example.places.databinding.ActivityAddActivityBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddActivityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddActivityBinding
    private var selectedImageUri: Uri? = null
    private var tempCameraImageUri: Uri? = null
    
    private val viewModel: AddActivityViewModel by viewModels {
        AddActivityViewModelFactory(
            (application as PlacesApplication).activityRepository,
            (application as PlacesApplication).userRepository,
            (application as PlacesApplication).publishedActivityRepository
        )
    }

    // Gallery image picker
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            updateImagePreview()
            Toast.makeText(this, "Image selected from gallery", Toast.LENGTH_SHORT).show()
        }
    }

    // Camera image capture
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = tempCameraImageUri
            updateImagePreview()
            Toast.makeText(this, "Photo captured", Toast.LENGTH_SHORT).show()
        }
    }

    // Permission request launcher
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val storageGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: false
        } else {
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
        }
        
        if (cameraGranted && storageGranted) {
            showImagePickerDialog()
        } else {
            Toast.makeText(this, "Permissions required for image selection", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, AddActivityActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
        setupClickListeners()
        setupDateField()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        
        // Set default date to today
        updateDateField(Date())
    }

    private fun observeViewModel() {
        viewModel.selectedDate.observe(this) { date ->
            updateDateField(date)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnAddActivity.isEnabled = !isLoading
            binding.btnAddActivity.text = if (isLoading) "Saving..." else "+ Add Activity"
        }

        viewModel.activitySaved.observe(this) { saved ->
            if (saved) {
                Toast.makeText(this, "Activity saved successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnAddActivity.setOnClickListener {
            saveActivity()
        }

        binding.btnAddPhoto.setOnClickListener {
            checkPermissionsAndShowImagePicker()
        }

        binding.btnAddLocation.setOnClickListener {
            // TODO: Implement location picker
            Toast.makeText(this, "Location picker coming soon", Toast.LENGTH_SHORT).show()
        }
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
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        
        AlertDialog.Builder(this)
            .setTitle("Select Image")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                    2 -> dialog.dismiss()
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
            // Update the button text to show image is selected
            binding.btnAddPhoto.text = "✓ Photo Selected"
            binding.btnAddPhoto.setIconResource(android.R.drawable.ic_menu_gallery)
        }
    }

    private fun setupDateField() {
        binding.etDate.setOnClickListener {
            showDatePicker()
        }
        
        binding.tilDate.setEndIconOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val currentDate = viewModel.selectedDate.value ?: Date()
        val calendar = Calendar.getInstance().apply { time = currentDate }
        
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                viewModel.setSelectedDate(selectedCalendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        datePickerDialog.show()
    }

    private fun updateDateField(date: Date) {
        val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        binding.etDate.setText(formatter.format(date))
    }

    private fun saveActivity() {
        val location = binding.etLocation.text?.toString()?.trim() ?: ""
        val notes = binding.etNotes.text?.toString()?.trim() ?: ""
        val selectedDate = viewModel.selectedDate.value ?: Date()

        // Clear any previous errors
        binding.tilLocation.error = null

        // Validate input
        if (location.isEmpty()) {
            binding.tilLocation.error = "Location is required"
            binding.etLocation.requestFocus()
            return
        }

        // Navigate to CreateActivityActivity with the entered data
        val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val formattedDate = formatter.format(selectedDate)
        
        val intent = com.example.places.ui.createactivity.CreateActivityActivity.newIntent(this).apply {
            putExtra(com.example.places.ui.createactivity.CreateActivityActivity.EXTRA_LOCATION, location)
            putExtra(com.example.places.ui.createactivity.CreateActivityActivity.EXTRA_NOTES, notes)
            putExtra(com.example.places.ui.createactivity.CreateActivityActivity.EXTRA_DATE, formattedDate)
            // Pass the selected image URI if available
            selectedImageUri?.let { uri ->
                putExtra(com.example.places.ui.createactivity.CreateActivityActivity.EXTRA_IMAGE_URI, uri.toString())
            }
        }
        startActivity(intent)
        
        // Activity will be saved only when user publishes from EntryPreviewActivity
    }
}
