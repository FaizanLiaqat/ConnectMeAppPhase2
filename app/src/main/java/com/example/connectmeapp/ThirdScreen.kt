package com.example.connectmeapp

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ThirdScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_third_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val registerText = findViewById<TextView>(R.id.already_login)
        registerText.setOnClickListener {
            val intent = Intent(this@ThirdScreen, SecondScreen::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this, R.anim.slide_in_left, R.anim.slide_out_right
            )

            startActivity(intent, options.toBundle())
            finish()
        }
    }
}