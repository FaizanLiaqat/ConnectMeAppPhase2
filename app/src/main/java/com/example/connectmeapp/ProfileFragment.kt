package com.example.connectmeapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connectmeapp.R

class ProfileFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var username: TextView
    private lateinit var bio: TextView
    private lateinit var postsCount: TextView
    private lateinit var followersCount: TextView
    private lateinit var followingCount: TextView
    private lateinit var editProfileIcon: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var profileAdapter: ProfileAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // 🔹 Initialize UI Elements
        profileImage = view.findViewById(R.id.profile_image)
        username = view.findViewById(R.id.profile_username)
        bio = view.findViewById(R.id.profile_bio)
        postsCount = view.findViewById(R.id.posts_count)
        followersCount = view.findViewById(R.id.followers_count)
        followingCount = view.findViewById(R.id.following_count)
        editProfileIcon = view.findViewById(R.id.edit_profile_icon)
        recyclerView = view.findViewById(R.id.profile_recycler_view)

        // 🔹 Load User Data (For now, use placeholders)
        username.text = "John Doe"
        bio.text = "🌍 Traveler | 📸 Photographer | 📖 Blogger"
        profileImage.setImageResource(R.drawable.profile_placeholder)

        // 🔹 Dummy Stats (Updated with random numbers)
        postsCount.text = "58"  // Example: 58 posts
        followersCount.text = "4.5K"  // Example: 4,500 followers
        followingCount.text = "328"  // Example: Following 328 people

        // 🔹 Setup RecyclerView for Posts
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        profileAdapter = ProfileAdapter(getSamplePosts())
        recyclerView.adapter = profileAdapter

        // 🔹 Handle Edit Profile Click
        editProfileIcon.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        return view
    }

    // ✅ Dummy posts data (More than 25 posts for testing)
    private fun getSamplePosts(): List<Int> {
        return listOf(
            R.drawable.profile_placeholder, R.drawable.profile_placeholder, R.drawable.profile_placeholder,
            R.drawable.profile_placeholder, R.drawable.profile_placeholder, R.drawable.profile_placeholder,
            R.drawable.profile_placeholder, R.drawable.profile_placeholder, R.drawable.profile_placeholder,
            R.drawable.profile_placeholder, R.drawable.profile_placeholder, R.drawable.profile_placeholder,
            R.drawable.profile_placeholder, R.drawable.profile_placeholder, R.drawable.profile_placeholder,
            R.drawable.profile_placeholder, R.drawable.profile_placeholder, R.drawable.profile_placeholder,
            R.drawable.profile_placeholder, R.drawable.profile_placeholder, R.drawable.profile_placeholder,
            R.drawable.profile_placeholder, R.drawable.profile_placeholder, R.drawable.profile_placeholder,
            R.drawable.profile_placeholder, R.drawable.profile_placeholder, R.drawable.profile_placeholder,
            R.drawable.profile_placeholder, R.drawable.profile_placeholder, R.drawable.profile_placeholder
        )
    }
}



