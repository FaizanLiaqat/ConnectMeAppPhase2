package com.example.connectmeapp

import android.app.ActivityOptions
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.ByteArrayOutputStream

class EditProfileActivity : AppCompatActivity() {

    private lateinit var profileImage: ImageView
    private lateinit var cameraIcon: ImageView
    private lateinit var currentUsername: TextView
    private lateinit var nameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var contactNumberEditText: EditText
    private lateinit var bioEditText: EditText
    private lateinit var btnDone: TextView

    private var selectedImageUri: Uri? = null
    private var encodedImage: String = ""
    private val TAG = "EditProfileActivity"
    private var isNewUser = false

    // Image picker launcher
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            profileImage.setImageURI(it)
            // Hide the camera icon when an image is selected
            cameraIcon.alpha = 0f

            // Convert the selected image to Base64 encoded string
            encodeImageToBase64(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Check if this is a new user
        isNewUser = intent.getBooleanExtra("NEW_USER", false)

        // Initialize UI elements
        profileImage = findViewById(R.id.profileImage)
        cameraIcon = findViewById(R.id.cameraIcon)
        currentUsername = findViewById(R.id.currentUsername)
        nameEditText = findViewById(R.id.nameEditText)
        usernameEditText = findViewById(R.id.usernameEditText)
        contactNumberEditText = findViewById(R.id.contactNumberEditText)
        bioEditText = findViewById(R.id.bioEditText)
        btnDone = findViewById(R.id.btnDone)

        // Load current user data from Firebase
        loadUserData()

        // Set up click listeners
        findViewById<FrameLayout>(R.id.profileImageContainer).setOnClickListener {
            pickImage.launch("image/*")
        }

        btnDone.setOnClickListener {
            saveUserData()
        }
    }

    private fun loadUserData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.e(TAG, "No user is currently signed in")
            Toast.makeText(this, "Error: No user signed in", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid
        val database = FirebaseDatabase.getInstance("https://faizan1ahmed1smda2-default-rtdb.asia-southeast1.firebasedatabase.app")
        val userRef = database.getReference("users").child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userData = snapshot.value as? Map<*, *>
                    if (userData != null) {
                        val name = userData["name"] as? String ?: ""
                        val uname = userData["username"] as? String ?: ""
                        val phoneNumber = userData["phoneNumber"] as? String ?: ""
                        val bio = userData["bio"] as? String ?: ""
                        val profileImageUrl = userData["profileImageUrl"] as? String ?: ""

                        currentUsername.text = uname

                        // For new users, use hints; for existing users, pre-fill the fields.
                        if (isNewUser) {
                            if (name.isNotEmpty()) nameEditText.hint = name
                            if (uname.isNotEmpty()) usernameEditText.hint = uname
                            if (phoneNumber.isNotEmpty()) contactNumberEditText.hint = phoneNumber
                            if (bio.isNotEmpty()) bioEditText.hint = bio
                        } else {
                            nameEditText.setText(name)
                            usernameEditText.setText(uname)
                            contactNumberEditText.setText(phoneNumber)
                            bioEditText.setText(bio)
                        }

                        // Load profile image if available
                        if (profileImageUrl.isNotEmpty()) {
                            try {
                                val imageBytes = Base64.decode(profileImageUrl, Base64.DEFAULT)
                                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                profileImage.setImageBitmap(decodedImage)
                                cameraIcon.alpha = 0f
                                encodedImage = profileImageUrl
                            } catch (e: Exception) {
                                Log.e(TAG, "Error decoding profile image", e)
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "User data not found in database")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database error: ${error.message}")
                Toast.makeText(baseContext, "Error loading user data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun encodeImageToBase64(imageUri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            // Scale down the bitmap to reduce size
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true)
            val baos = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
            val imageBytes = baos.toByteArray()
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)
            Log.d(TAG, "Image encoded successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error encoding image", e)
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.e(TAG, "No user is currently signed in")
            Toast.makeText(this, "Error: No user signed in", Toast.LENGTH_SHORT).show()
            return
        }

        // Get input values; if a field is left empty, do not update it.
        val nameInput = nameEditText.text.toString().trim()
        val usernameInput = usernameEditText.text.toString().trim()
        val phoneInput = contactNumberEditText.text.toString().trim()
        val bioInput = bioEditText.text.toString().trim()

        // For fields that are left empty, we want to keep the old value.
        // First, get a snapshot of current data.
        val userId = currentUser.uid
        val database = FirebaseDatabase.getInstance("https://faizan1ahmed1smda2-default-rtdb.asia-southeast1.firebasedatabase.app")
        val userRef = database.getReference("users").child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentData = snapshot.value as? Map<*, *>
                val currentName = currentData?.get("name") as? String ?: ""
                val currentUsername = currentData?.get("username") as? String ?: ""
                val currentPhone = currentData?.get("phoneNumber") as? String ?: ""
                val currentBio = currentData?.get("bio") as? String ?: ""

                // Create updates only for non-empty input; otherwise, use the current data.
                val updates = hashMapOf<String, Any>(
                    "name" to if (nameInput.isNotEmpty()) nameInput else currentName,
                    "username" to if (usernameInput.isNotEmpty()) usernameInput else currentUsername,
                    "phoneNumber" to if (phoneInput.isNotEmpty()) phoneInput else currentPhone,
                    "bio" to if (bioInput.isNotEmpty()) bioInput else currentBio
                )
                // Update profile image only if a new image was selected.
                if (encodedImage.isNotEmpty()) {
                    updates["profileImageUrl"] = encodedImage
                }

                // If new user, mark as not new.
                if (isNewUser) {
                    updates["isNewUser"] = false
                }

                btnDone.isEnabled = false
                userRef.updateChildren(updates)
                    .addOnSuccessListener {
                        Log.d(TAG, "User data updated successfully")
                        Toast.makeText(this@EditProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()

                        // After updating user data, update any active story with new profile image.
                        if (encodedImage.isNotEmpty()) {
                            updateActiveStoriesProfileImage(userId, encodedImage)
                        }

                        // Navigate to MainActivity
                        val intent = Intent(this@EditProfileActivity, MainActivity::class.java)
                        val options = ActivityOptions.makeCustomAnimation(
                            this@EditProfileActivity, R.anim.slide_in_right, R.anim.slide_out_left
                        )
                        startActivity(intent, options.toBundle())
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error updating user data", e)
                        Toast.makeText(this@EditProfileActivity, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                        btnDone.isEnabled = true
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditProfileActivity, "Data retrieval cancelled: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Update the active stories (if any) with the new profile image.
    private fun updateActiveStoriesProfileImage(userId: String, newProfileImageUrl: String) {
        val storiesRef = FirebaseDatabase.getInstance().getReference("stories")
        // Query stories for this user. You might want to filter by timestamp if only active stories should be updated.
        storiesRef.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        // Update the field "userProfileImageUrl" in each story.
                        child.ref.child("userProfileImageUrl").setValue(newProfileImageUrl)
                    }
                }
                override fun onCancelled(error: DatabaseError) { }
            })
    }
}
