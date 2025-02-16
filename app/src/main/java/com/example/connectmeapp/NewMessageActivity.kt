package com.example.connectmeapp

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        // 🔙 Back Button Click
        findViewById<ImageView>(R.id.back_icon).setOnClickListener {
            finish()
        }
    }
}