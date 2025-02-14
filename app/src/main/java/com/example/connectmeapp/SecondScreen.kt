package com.example.connectmeapp

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecondScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val registerText = findViewById<TextView>(R.id.register)
        registerText.setOnClickListener {
            val intent = Intent(this@SecondScreen, ThirdScreen::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this, R.anim.slide_in_right, R.anim.slide_out_left
            )

            startActivity(intent, options.toBundle())
            finish()
        }
    }
}