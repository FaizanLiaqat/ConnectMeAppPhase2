package com.example.connectmeapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import de.hdodenhof.circleimageview.CircleImageView

class EditProfileActivity : AppCompatActivity() {

    private lateinit var profileImage: CircleImageView
    private lateinit var cameraIcon: ImageView
    private lateinit var currentUsername: TextView
    private lateinit var nameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var contactNumberEditText: EditText
    private lateinit var bioEditText: EditText
    private lateinit var btnDone: TextView

    private var selectedImageUri: Uri? = null

    // Image picker launcher
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            profileImage.setImageURI(it)
            // Hide the camera icon when an image is selected
            cameraIcon.alpha = 0f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Initialize UI elements
        profileImage = findViewById(R.id.profileImage)
        cameraIcon = findViewById(R.id.cameraIcon)
        currentUsername = findViewById(R.id.currentUsername)
        nameEditText = findViewById(R.id.nameEditText)
        usernameEditText = findViewById(R.id.usernameEditText)
        contactNumberEditText = findViewById(R.id.contactNumberEditText)
        bioEditText = findViewById(R.id.bioEditText)
        btnDone = findViewById(R.id.btnDone)

        // Load current user data
        loadUserData()

        // Set up click listeners
        findViewById<FrameLayout>(R.id.profileImageContainer).setOnClickListener {
            pickImage.launch("image/*")
        }

        btnDone.setOnClickListener {
            saveUserData()
            finish()
        }
    }

    private fun loadUserData() {
        // In a real app, you would load this data from your backend or local storage
        // For this example, we'll use placeholder data
        currentUsername.text = "john_doe"
        nameEditText.hint = "John Doe"
        usernameEditText.hint = "john_doe"
        contactNumberEditText.hint = "+1 123-456-7890"
        bioEditText.hint = "🌍 Traveler | 📸 Photographer | 📖 Blogger"

        // If you want to load actual data for editing, you would do it here:
        // nameEditText.setText(actualName)
        // usernameEditText.setText(actualUsername)
        // etc.
    }

    private fun saveUserData() {
        // In a real app, you would save this data to your backend or local storage
        val name = nameEditText.text.toString()
        val username = usernameEditText.text.toString()
        val contactNumber = contactNumberEditText.text.toString()
        val bio = bioEditText.text.toString()

        // Validate data
        if (name.isBlank() || username.isBlank()) {
            Toast.makeText(this, "Name and username cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Handle image upload (if selected)
        selectedImageUri?.let {
            // In a real app, you would upload this image to your server
            // For this example, we'll just show a toast
            Toast.makeText(this, "Profile image updated", Toast.LENGTH_SHORT).show()
        }

        // Show success message
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()

        // In a real app, you would save all this data to your model/database
        // For example:
        // val userModel = UserModel(name, username, contactNumber, bio, imageUrl)
        // userRepository.updateUser(userModel)
    }
}