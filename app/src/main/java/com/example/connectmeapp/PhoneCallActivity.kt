package com.example.connectmeapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PhoneCallActivity : AppCompatActivity() {

    private lateinit var username: TextView
    private lateinit var profileImage: ImageView
    private lateinit var callTime: TextView
    private lateinit var endCallButton: ImageView
    private lateinit var switchToVideoCall: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_call)

        // 🔹 Get User Data from Intent
        val userName = intent.getStringExtra("username") ?: "Unknown"
        val userImage = intent.getIntExtra("profileImage", R.drawable.profile_placeholder)

        // 🔹 Initialize UI Elements
        username = findViewById(R.id.call_username)
        profileImage = findViewById(R.id.call_profile_image)
        callTime = findViewById(R.id.call_time)
        endCallButton = findViewById(R.id.end_call)
        switchToVideoCall = findViewById(R.id.switch_to_video) // ✅ Added video call button

        // 🔹 Set Data
        username.text = userName
        profileImage.setImageResource(userImage)

        // 🔹 Handle End Call Button Click
        endCallButton.setOnClickListener {
            finish()  // Ends the call & closes activity
        }

        // 🔹 Handle Switch to Video Call Click
        switchToVideoCall.setOnClickListener {
            val intent = Intent(this, VideoCallActivity::class.java).apply {
                putExtra("username", userName)
                putExtra("profileImage", userImage)
            }
            startActivity(intent)
            finish() // Close phone call activity
        }
    }
}
