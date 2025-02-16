package com.example.connectmeapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class NotificationPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2 // 🔹 Two Tabs (DMs & Requests)

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) DMsFragment() else RequestsFragment()
    }
}
