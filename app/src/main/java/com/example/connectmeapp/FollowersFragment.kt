package com.example.connectmeapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FollowersFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: EditText
    private lateinit var adapter: FollowAdapter
    private val followersList = mutableListOf<UserModel>()
    private val displayedList = mutableListOf<UserModel>() // Filtered list
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
            View? {
        val view = inflater.inflate(R.layout.fragment_followers, container, false)

        recyclerView = view.findViewById(R.id.follow_recycler_view)
        searchBar = view.findViewById(R.id.search_bar)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        adapter = FollowAdapter(displayedList, currentUserId, { user ->
            val intent = Intent(requireContext(), ChatBoxActivity::class.java)
            intent.putExtra("username", user.username)
            intent.putExtra("profileImage", user.profileImageUrl)
            startActivity(intent)
        }, { user, isFollowing ->
            if (isFollowing) {
                unfollowUser(user.userId)
            } else {
                followUser(user.userId)
            }
        })

        recyclerView.adapter = adapter

        loadFollowers()

        // Search functionality
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterUsers(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return view
    }

    private fun loadFollowers() {
        val followersRef = FirebaseDatabase.getInstance()
            .getReference("followers")
            .child(currentUserId)

        followersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                followersList.clear()
                displayedList.clear()

                val followerUids = snapshot.children.mapNotNull { it.key }
                if (followerUids.isEmpty()) {
                    adapter.notifyDataSetChanged()
                    return
                }

                // Fetch all users in a single query
                FirebaseDatabase.getInstance().getReference("users")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(userSnapshot: DataSnapshot) {
                            for (child in userSnapshot.children) {
                                val user = child.getValue(UserModel::class.java)
                                if (user != null && followerUids.contains(user.userId)) {
                                    followersList.add(user)
                                    displayedList.add(user)
                                }
                            }
                            adapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("FollowersFragment", "Error fetching users: ${error.message}")
                        }
                    })
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error loading followers", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun followUser(targetUserId: String) {
        val followingRef = FirebaseDatabase.getInstance()
            .getReference("following")
            .child(currentUserId)
            .child(targetUserId)

        followingRef.setValue(true).addOnSuccessListener {
            FirebaseDatabase.getInstance()
                .getReference("followers")
                .child(targetUserId)
                .child(currentUserId)
                .setValue(true)
                .addOnSuccessListener {
                    // Update UI immediately
                    val user = followersList.find { it.userId == targetUserId }
                    if (user != null) {
                        displayedList.add(user)
                        adapter.notifyItemInserted(displayedList.size - 1)
                    }
                }
        }
    }

    private fun unfollowUser(targetUserId: String) {
        val followingRef = FirebaseDatabase.getInstance()
            .getReference("following")
            .child(currentUserId)
            .child(targetUserId)

        followingRef.removeValue().addOnSuccessListener {
            FirebaseDatabase.getInstance()
                .getReference("followers")
                .child(targetUserId)
                .child(currentUserId)
                .removeValue()
                .addOnSuccessListener {
                    // Remove from list and update UI
                    followersList.removeAll { it.userId == targetUserId }
                    displayedList.removeAll { it.userId == targetUserId }
                    adapter.notifyDataSetChanged()
                }
        }
    }

    private fun filterUsers(query: String) {
        displayedList.clear()
        if (query.isEmpty()) {
            displayedList.addAll(followersList)
        } else {
            displayedList.addAll(followersList.filter {
                it.username.contains(query, ignoreCase = true)
            })
        }
        adapter.notifyDataSetChanged()
    }
}

