package com.example.connectmeapp

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

class RequestsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: EditText
    private lateinit var requestAdapter: RequestAdapter
    private val requestList = mutableListOf<UserModel>()
    private val filteredList = mutableListOf<UserModel>()
    private val currentUserId: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val dbRef = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_requests, container, false)
        recyclerView = view.findViewById(R.id.requestsRecyclerView)
        searchBar = view.findViewById(R.id.search_requests)

        recyclerView.layoutManager = LinearLayoutManager(context)
        requestAdapter = RequestAdapter(filteredList) { user, isAccepted ->
            handleFollowRequest(user, isAccepted)
        }
        recyclerView.adapter = requestAdapter

        fetchFollowRequests()
        setupSearch()

        return view
    }

    private fun fetchFollowRequests() {
        currentUserId?.let { uid ->
            dbRef.child("follow_requests").child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    requestList.clear()
                    for (request in snapshot.children) {
                        val user = request.getValue(UserModel::class.java)
                        user?.let { requestList.add(it) }
                    }
                    filteredList.clear()
                    filteredList.addAll(requestList)
                    requestAdapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("RequestsFragment", "Database error: ${error.message}")
                }
            })
        }
    }

    private fun setupSearch() {
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterRequests(s.toString())
            }
        })
    }

    private fun filterRequests(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(requestList)
        } else {
            filteredList.addAll(requestList.filter { it.username.startsWith(query, ignoreCase = true) })
        }
        requestAdapter.notifyDataSetChanged()
    }

    private fun handleFollowRequest(user: UserModel, isAccepted: Boolean) {
        currentUserId?.let { uid ->
            val requestRef = dbRef.child("follow_requests").child(uid).child(user.userId)

            if (isAccepted) {
                // Fetch current user details to store in sender's "following" list
                dbRef.child("users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val currentUser = snapshot.getValue(UserModel::class.java)
                        if (currentUser != null) {
                            val followersRef = dbRef.child("followers").child(uid).child(user.userId)
                            val followingRef = dbRef.child("following").child(user.userId).child(uid)

                            // Store full user models instead of 'true'
                            followersRef.setValue(user).addOnCompleteListener {
                                followingRef.setValue(currentUser)
                                requestRef.removeValue()
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            } else {
                requestRef.removeValue()
            }
        }
    }

}