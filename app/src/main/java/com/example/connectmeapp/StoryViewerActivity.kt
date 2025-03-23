package com.example.connectmeapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StoryViewerActivity : AppCompatActivity() {
    private lateinit var storyImage: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var usernameText: TextView
    private lateinit var closeButton: ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storiesReference: DatabaseReference

    private var storyId: String? = null
    private var userId: String? = null
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_viewer)

        // Get intent extras
        storyId = intent.getStringExtra("STORY_ID")
        userId = intent.getStringExtra("USER_ID")

        if (storyId == null || userId == null) {
            Toast.makeText(this, "Error loading story", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storiesReference = database.getReference("stories")

        // Initialize views
        storyImage = findViewById(R.id.story_image)
        progressBar = findViewById(R.id.progress_bar)
        usernameText = findViewById(R.id.username_text)
        closeButton = findViewById(R.id.close_button)

        // Set click listener for close button
        closeButton.setOnClickListener { finish() }

        // Load the story
        loadStory()
    }

    private fun loadStory() {
        progressBar.visibility = View.VISIBLE

        // Query all stories for this user
        val query = storiesReference.orderByChild("userId").equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userStories = mutableListOf<StoryModel>()

                for (storySnapshot in snapshot.children) {
                    val story = storySnapshot.getValue(StoryModel::class.java)
                    if (story != null && story.isValid()) {
                        userStories.add(story)
                    }
                }

                // Sort stories by timestamp (newest first)
                userStories.sortByDescending { it.timestamp }

                if (userStories.isNotEmpty()) {
                    // Find the current story or use the first one
                    val currentStoryIndex = userStories.indexOfFirst { it.id == storyId }
                    val currentStory = if (currentStoryIndex >= 0) {
                        userStories[currentStoryIndex]
                    } else {
                        userStories.first()
                    }

                    // Display the story
                    displayStory(currentStory)

                    // Mark story as viewed
                    val currentUserId = auth.currentUser?.uid
                    if (currentUserId != null && !currentStory.viewedBy.contains(currentUserId)) {
                        storiesReference.child(currentStory.id).child("viewedBy")
                            .child(currentUserId).setValue(true)
                    }

                    // Start auto-dismiss timer (5 seconds per story)
                    startStoryTimer()
                } else {
                    Toast.makeText(this@StoryViewerActivity, "No stories available",
                        Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@StoryViewerActivity, "Error loading story: ${error.message}",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun displayStory(story: StoryModel) {
        // Display username
        usernameText.text = story.username

        // Display the story image
        if (story.imageBase64.isNotEmpty()) {
            try {
                val imageBytes = Base64.decode(story.imageBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                storyImage.setImageBitmap(bitmap)
            } catch (e: Exception) {
                storyImage.setImageResource(R.drawable.profile_placeholder)
            }
        } else {
            storyImage.setImageResource(R.drawable.profile_placeholder)
        }

        progressBar.visibility = View.GONE
    }

    private fun startStoryTimer() {
        timer?.cancel()

        timer = object : CountDownTimer(5000, 5000) {
            override fun onTick(millisUntilFinished: Long) {
                // Not needed
            }

            override fun onFinish() {
                finish()
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}