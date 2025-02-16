package com.example.connectmeapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class NotificationActivity: AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: NotificationPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // 🔹 Initialize UI Elements
        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)

        // 🔹 Setup ViewPager Adapter
        adapter = NotificationPagerAdapter(this)
        viewPager.adapter = adapter

        // 🔹 Connect TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "DMs" else "Requests" // ✅ Prevents Auto-Capitalization
        }.attach()


        // 🔙 Back Button Click
        findViewById<ImageView>(R.id.back_icon).setOnClickListener {
            finish()
        }

        // ✉️ New Message Click (Open New Chat Activity)
        findViewById<ImageView>(R.id.new_message_icon).setOnClickListener {
            startActivity(Intent(this, NewMessageActivity::class.java))
        }
    }
}

