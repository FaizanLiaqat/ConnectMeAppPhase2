package com.example.connectmeapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ChatBoxActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_box)

        // 🔹 Get Data from Intent
        val username = intent.getStringExtra("username")
        val profileImage = intent.getIntExtra("profileImage", R.drawable.profile_placeholder)

        // 🔹 Set Data in UI
        findViewById<TextView>(R.id.chat_username).text = username
        findViewById<ImageView>(R.id.chat_profile_image).setImageResource(profileImage)

        // 🔙 Handle Back Button Click
        findViewById<ImageView>(R.id.back_icon).setOnClickListener {
            finish()
        }
    }
}
