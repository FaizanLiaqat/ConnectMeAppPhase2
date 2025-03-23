package com.example.connectmeapp

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var username: TextView
    private lateinit var bio: TextView
    private lateinit var postsCount: TextView
    private lateinit var followersCount: TextView
    private lateinit var followingCount: TextView
    private lateinit var editProfileIcon: ImageView
    private var followButton: Button? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var profileAdapter: ProfileAdapter

    private var postsList: MutableList<String> = mutableListOf()

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val profileUserId: String by lazy {
        arguments?.getString("profileUserId") ?: currentUserId
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        profileImage = view.findViewById(R.id.profile_image)
        username = view.findViewById(R.id.profile_username)
        bio = view.findViewById(R.id.profile_bio)
        postsCount = view.findViewById(R.id.posts_count)
        followersCount = view.findViewById(R.id.followers_count)
        followingCount = view.findViewById(R.id.following_count)
        editProfileIcon = view.findViewById(R.id.edit_profile_icon)
        recyclerView = view.findViewById(R.id.profile_recycler_view)

        loadUserData()
        loadUserPosts()

        followersCount.setOnClickListener { openFollowActivity("Followers", username.text.toString()) }
        followingCount.setOnClickListener { openFollowActivity("Following", username.text.toString()) }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        profileAdapter = ProfileAdapter(postsList)
        recyclerView.adapter = profileAdapter

        if (profileUserId == currentUserId) {
            editProfileIcon.isVisible = true
            editProfileIcon.setOnClickListener {
                startActivity(Intent(requireContext(), EditProfileActivity::class.java))
            }
        } else {
            editProfileIcon.isVisible = false
            followButton = Button(requireContext()).apply {
                textSize = 16f
                setPadding(16, 8, 16, 8)
            }
            (view as ViewGroup).addView(followButton)
            loadFollowStatus()
            followButton?.setOnClickListener { toggleFollow() }
        }

        return view
    }

    private fun loadUserData() {
        val usersRef = FirebaseDatabase.getInstance().getReference("users").child(profileUserId)
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserModel::class.java)
                    username.text = user?.username ?: "Unknown"
                    bio.text = user?.bio ?: ""
                    postsCount.text = "0"
                    followersCount.text = "0"
                    followingCount.text = "0"

                    user?.profileImageUrl?.let {
                        val imageBytes = Base64.decode(it, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        profileImage.setImageBitmap(bitmap)
                    } ?: profileImage.setImageResource(R.drawable.profile_placeholder)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadUserPosts() {
        val postsRef = FirebaseDatabase.getInstance().getReference("posts").child(profileUserId)
        postsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postsList.clear()
                for (postSnapshot in snapshot.children) {
                    val imageUrl = postSnapshot.child("imageUrl").getValue(String::class.java)
                    imageUrl?.let { postsList.add(it) }
                }
                postsCount.text = postsList.size.toString()
                profileAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadFollowStatus() {
        val followingRef = FirebaseDatabase.getInstance()
            .getReference("following")
            .child(currentUserId)
            .child(profileUserId)
        followingRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                followButton?.text = if (snapshot.exists()) "Following" else "Follow"
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun toggleFollow() {
        val followingRef = FirebaseDatabase.getInstance()
            .getReference("following")
            .child(currentUserId)
            .child(profileUserId)
        followingRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    followingRef.removeValue().addOnSuccessListener {
                        FirebaseDatabase.getInstance()
                            .getReference("followers")
                            .child(profileUserId)
                            .child(currentUserId)
                            .removeValue()
                        followButton?.text = "Follow"
                    }
                } else {
                    followingRef.setValue(true).addOnSuccessListener {
                        FirebaseDatabase.getInstance()
                            .getReference("followers")
                            .child(profileUserId)
                            .child(currentUserId)
                            .setValue(true)
                        followButton?.text = "Following"
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun openFollowActivity(tabType: String, userName: String) {
        val intent = Intent(requireContext(), FollowActivity::class.java)
        intent.putExtra("tabType", tabType)
        intent.putExtra("username", userName)
        startActivity(intent)
    }
}