package com.example.connectmeapp

import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class AddPostActivity : AppCompatActivity() {
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var selectedImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        // Hide the action bar since we're implementing our own top bar
        supportActionBar?.hide()

        // Setup top bar cross icon click
        findViewById<ImageView>(R.id.cross_icon).setOnClickListener {
            finish()
        }

        // In AddPostActivity.kt
        // Modify the Next button click listener:
        findViewById<TextView>(R.id.next_button).setOnClickListener {
            // Navigate to edit post activity with selected image
            val intent = Intent(this, EditPostActivity::class.java)
            // Get the URI of the currently selected image
            val selectedImageUri = galleryAdapter.getSelectedImageUri()
            intent.putExtra("IMAGE_URI", selectedImageUri.toString())
            startActivity(intent)
        }

        // Setup selected image view
        selectedImageView = findViewById(R.id.selected_image)

        // Setup gallery RecyclerView
        setupGalleryRecyclerView()

        // Setup camera icon click
        setupCameraIcon()
    }


    private fun setupCameraIcon() {
        val cameraIcon = findViewById<ImageView>(R.id.camera_icon)
        cameraIcon.setOnClickListener {
            // Open camera activity
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupGalleryRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.gallery_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 4)

        // Get images from the device
        val galleryImages = getGalleryImages()

        // Set initial selected image if available
        if (galleryImages.isNotEmpty()) {
            updateSelectedImage(galleryImages[0])
        }

        // Create and set adapter
        galleryAdapter = GalleryAdapter(galleryImages) { imageUri ->
            updateSelectedImage(imageUri)
        }
        recyclerView.adapter = galleryAdapter
    }

    private fun updateSelectedImage(imageUri: Uri) {
        selectedImageView.setImageURI(imageUri)
    }

    private fun getGalleryImages(): List<Uri> {
        val images = mutableListOf<Uri>()

        // Query for all images with broader criteria
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC" // Sort by newest first

        // Query both external and internal storage
        val externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        try {
            contentResolver.query(
                externalContentUri,
                projection,
                null, // No selection filter
                null, // No selection arguments
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(
                        externalContentUri,
                        id
                    )
                    images.add(contentUri)
                }
            }
        } catch (e: Exception) {
            // Log error to help debug
            Log.e("AddPostActivity", "Error retrieving gallery images: ${e.message}")
        }

        return images
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImage = data.data
            selectedImage?.let {
                updateSelectedImage(it)
            }
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                setupGalleryRecyclerView()
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For Android 6-12
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                setupGalleryRecyclerView()
            }
        } else {
            // For older Android versions
            setupGalleryRecyclerView()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            setupGalleryRecyclerView()
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val PICK_IMAGE_REQUEST = 1
    }


    override fun onResume() {
        super.onResume()

        // Refresh gallery images when returning to this activity
        setupGalleryRecyclerView()
    }
}