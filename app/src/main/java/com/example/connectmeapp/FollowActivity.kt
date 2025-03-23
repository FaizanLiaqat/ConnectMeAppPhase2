package com.example.connectmeapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator

class FollowActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var backIcon: ImageView
    private lateinit var usernameText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follow)

        // Retrieve data from Intent
        val username = intent.getStringExtra("username") ?: "User"
        val tabType = intent.getStringExtra("tabType") ?: "Followers"  // Default tab

        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)
        backIcon = findViewById(R.id.back_icon)
        usernameText = findViewById(R.id.username_text)

        usernameText.text = username

        // Set up ViewPager adapter
        val adapter = FollowPagerAdapter(this)
        viewPager.adapter = adapter

        // Connect TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Followers" else "Following"
        }.attach()

        // Set default tab based on intent
        if (tabType == "Following") {
            viewPager.setCurrentItem(1, false)
        } else {
            viewPager.setCurrentItem(0, false)
        }

        backIcon.setOnClickListener { finish() }
    }
}
