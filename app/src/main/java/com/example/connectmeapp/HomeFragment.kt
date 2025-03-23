package com.example.connectmeapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var postsRecyclerView: RecyclerView
    private lateinit var postsAdapter: PostAdapter
    private val postsList = mutableListOf<PostModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Handle notification icon click to open NotificationActivity
        val notificationIcon = view.findViewById<ImageView>(R.id.notification_icon)
        notificationIcon.setOnClickListener {
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)
        }

        // Embed StoryFragment in the story container
        val storyFragment = StoryFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.story_container, storyFragment)
            .commit()

        // Set up posts RecyclerView
        postsRecyclerView = view.findViewById(R.id.posts_recycler_view)
        postsRecyclerView.layoutManager = LinearLayoutManager(context)
        postsAdapter = PostAdapter(postsList)
        postsRecyclerView.adapter = postsAdapter

        // Load posts from Firebase
        loadPosts()

        return view
    }


    private fun loadPosts() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val usersRef = FirebaseDatabase.getInstance().getReference("users/$currentUserId/following")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val followedUsers = snapshot.children.mapNotNull { it.key }
                val postsRef = FirebaseDatabase.getInstance().getReference("posts")

                postsRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        postsList.clear()
                        for (child in snapshot.children) {
                            val post = child.getValue(PostModel::class.java)
                            if (post != null && followedUsers.contains(post.userId)) {
                                postsList.add(post)
                            }
                        }
                        postsList.sortByDescending { it.timestamp }
                        postsAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Error loading posts: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}
