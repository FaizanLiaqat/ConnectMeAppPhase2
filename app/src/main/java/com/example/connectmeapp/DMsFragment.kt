package com.example.connectmeapp

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DMsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: EditText
    private lateinit var adapter: DMAdapter
    private val userList = mutableListOf<UserModel>()
    private val dbRef = FirebaseDatabase.getInstance().reference
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dms, container, false)

        searchBar = view.findViewById(R.id.search_dm)
        recyclerView = view.findViewById(R.id.dm_recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = DMAdapter(userList) { user -> openChatBox(user) }
        recyclerView.adapter = adapter

        fetchFollowedUsers()

        // Search Filtering
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterUsers(s.toString())
            }
        })

        return view
    }

    private fun fetchFollowedUsers() {
        if (currentUserId == null) return

        dbRef.child("following").child(currentUserId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()

                for (followedSnapshot in snapshot.children) {
                    val followedUserId = followedSnapshot.key
                    if (followedUserId != null) {
                        fetchUserDetails(followedUserId)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DMsFragment", "Error fetching following list: ${error.message}")
            }
        })
    }

    private fun fetchUserDetails(userId: String) {
        dbRef.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java)
                if (user != null) {
                    userList.add(user)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DMsFragment", "Error fetching user details: ${error.message}")
            }
        })
    }

    private fun filterUsers(query: String) {
        val filteredList = if (query.isEmpty()) {
            userList
        } else {
            userList.filter { it.username.startsWith(query, ignoreCase = true) }
        }
        adapter.updateList(filteredList)
    }

    private fun openChatBox(user: UserModel) {
        val intent = Intent(requireContext(), ChatBoxActivity::class.java).apply {
            putExtra("username", user.username)
            putExtra("profileImage", user.profileImageUrl)
            putExtra("otherUserId", user.userId)
        }
        val options = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.slide_in_right, R.anim.slide_out_left)
        startActivity(intent, options.toBundle())
    }
}

