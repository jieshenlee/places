package com.example.places.ui.addactivity

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.places.PlacesApplication
import com.example.places.databinding.ActivityAddActivityBinding
import java.text.SimpleDateFormat
import java.util.*

class AddActivityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddActivityBinding
    
    private val viewModel: AddActivityViewModel by viewModels {
        AddActivityViewModelFactory(
            (application as PlacesApplication).activityRepository,
            (application as PlacesApplication).userRepository
        )
    }

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // TODO: Handle image selection
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show()
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
            startActivity(Intent(this, com.example.places.ui.createactivity.CreateActivityActivity::class.java))
        }

        binding.btnAddPhoto.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.btnAddLocation.setOnClickListener {
            // TODO: Implement location picker
            Toast.makeText(this, "Location picker coming soon", Toast.LENGTH_SHORT).show()
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

        // Save the activity
        viewModel.saveActivity(
            location = location,
            notes = notes,
            date = selectedDate
        )
    }
}
