package com.example.connectmeapp

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var viewFinder: PreviewView
    private var capturedImageUri: Uri? = null
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var postText: TextView
    private lateinit var storyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // Hide the action bar
        supportActionBar?.hide()

        // Initialize UI components
        viewFinder = findViewById(R.id.viewFinder)
        postText = findViewById(R.id.post_text)
        storyText = findViewById(R.id.story_text)

        // Setup cross icon to go back
        findViewById<ImageView>(R.id.cross_icon).setOnClickListener {
            finish()
        }

        // Setup Next button to go to edit post
        findViewById<TextView>(R.id.next_button).setOnClickListener {
            if (capturedImageUri != null) {
                val intent = Intent(this, EditPostActivity::class.java)
                intent.putExtra("IMAGE_URI", capturedImageUri.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this, "Take a photo first", Toast.LENGTH_SHORT).show()
            }
        }

        // Setup flip camera button
        findViewById<ImageView>(R.id.flip_camera_icon).setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            startCamera()
        }

        // Setup gallery button
        findViewById<ImageView>(R.id.gallery_icon).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Setup capture button
        findViewById<ImageView>(R.id.capture_button).setOnClickListener {
            takePhoto()
        }

        // Setup ViewPager and TabLayout for Post/Story tabs
        setupViewPager()

        // Check if we should switch camera based on intent extras
        if (intent.getStringExtra("CAMERA_FACING") == "SWITCH") {
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        }

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Initialize camera executor
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun setupViewPager() {
        viewPager = findViewById(R.id.view_pager)

        // Create adapters for the ViewPager2
        val adapter = CameraModePagerAdapter(this)
        viewPager.adapter = adapter

        // Set up viewpager for swipe between post and story
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateTabSelection(position)
            }
        })

        // Set up tab click listeners
        postText.setOnClickListener {
            viewPager.currentItem = 0
        }

        storyText.setOnClickListener {
            viewPager.currentItem = 1
        }

        // Set initial selection
        updateTabSelection(0)
    }

    private fun updateTabSelection(position: Int) {
        if (position == 0) {
            // Post selected
            postText.setTextColor(ContextCompat.getColor(this, R.color.dark_brown))
            postText.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 18f)
            storyText.setTextColor(ContextCompat.getColor(this, R.color.gray))
            storyText.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16f)
        } else {
            // Story selected
            storyText.setTextColor(ContextCompat.getColor(this, R.color.dark_brown))
            storyText.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 18f)
            postText.setTextColor(ContextCompat.getColor(this, R.color.gray))
            postText.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16f)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            // ImageCapture
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        // Get a reference to the image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped name and MediaStore entry
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ConnectMe")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    capturedImageUri = output.savedUri

                    // Show the captured image
                    val capturedImageView = findViewById<ImageView>(R.id.captured_image)
                    capturedImageView.setImageURI(capturedImageUri)
                    capturedImageView.visibility = View.VISIBLE
                    viewFinder.visibility = View.GONE

                    // Enable the Next button
                    findViewById<TextView>(R.id.next_button).isEnabled = true
                }
            }
        )
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImage = data.data
            selectedImage?.let {
                capturedImageUri = it

                // Display the selected image
                val capturedImageView = findViewById<ImageView>(R.id.captured_image)
                capturedImageView.setImageURI(capturedImageUri)
                capturedImageView.visibility = View.VISIBLE
                viewFinder.visibility = View.GONE

                // Enable the Next button
                findViewById<TextView>(R.id.next_button).isEnabled = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraActivity"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val PICK_IMAGE_REQUEST = 1
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}