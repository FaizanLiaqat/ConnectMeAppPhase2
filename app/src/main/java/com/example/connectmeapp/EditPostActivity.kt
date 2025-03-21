package com.example.connectmeapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EditPostActivity : AppCompatActivity() {
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)

        // Hide the action bar
        supportActionBar?.hide()

        // Get the image URI if it exists
        if (intent.hasExtra("IMAGE_URI")) {
            val uriString = intent.getStringExtra("IMAGE_URI")
            if (!uriString.isNullOrEmpty()) {
                imageUri = Uri.parse(uriString)
                imageUri?.let {
                    findViewById<ImageView>(R.id.post_image).setImageURI(it)
                }
            }
        }

        // Setup back button
        findViewById<ImageView>(R.id.back_icon).setOnClickListener {
            finish()
        }

        // Setup share button
        findViewById<Button>(R.id.share_button).setOnClickListener {
            // Get caption
            val caption = findViewById<EditText>(R.id.caption_input).text.toString()

            // In a real app, you would upload the image and post details to a server
            // For this example, we'll just go back to the main activity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }
}