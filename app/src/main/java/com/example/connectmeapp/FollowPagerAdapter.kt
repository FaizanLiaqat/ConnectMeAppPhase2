package com.example.connectmeapp
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FollowPagerAdapter(activity: FollowActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2 // Two tabs: Followers & Following
    override fun createFragment(position: Int): Fragment {
        return if (position == 0) FollowersFragment() else FollowingFragment()
    }
}
