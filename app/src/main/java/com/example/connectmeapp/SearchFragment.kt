package com.example.connectmeapp


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connectmeapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {

    private lateinit var searchBar: EditText
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var recentRecyclerView: RecyclerView
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var recentAdapter: RecentSearchAdapter
    private val userList = mutableListOf<UserModel>()
    private val recentSearchList = mutableListOf<String>()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val pendingRequests = mutableMapOf<String, Boolean>() // Track follow requests

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        searchBar = view.findViewById(R.id.search_bar)
        searchRecyclerView = view.findViewById(R.id.search_recycler_view)
        recentRecyclerView = view.findViewById(R.id.recent_recycler_view)

        setupRecyclerViews()
        loadRecentSearches()
        setupSearchListener()

        return view
    }

    private fun setupRecyclerViews() {
        // Setup search RecyclerView
        searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchAdapter = SearchAdapter(userList, { user ->
            sendFollowRequest(user.userId)
            saveToRecentSearches(user.username)
        }, { user ->
            sendFollowRequest(user.userId)
        }, pendingRequests) // Pass pending requests map
        searchRecyclerView.adapter = searchAdapter

        // Setup recent searches RecyclerView
        recentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recentAdapter = RecentSearchAdapter(recentSearchList) { username ->
            removeFromRecentSearches(username)
        }
        recentRecyclerView.adapter = recentAdapter
    }

    private fun setupSearchListener() {
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    searchRecyclerView.visibility = View.GONE
                    recentRecyclerView.visibility = View.VISIBLE
                } else {
                    searchRecyclerView.visibility = View.VISIBLE
                    recentRecyclerView.visibility = View.GONE
                    searchUsers(query)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun searchUsers(query: String) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")
        usersRef.orderByChild("username").startAt(query).endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (child in snapshot.children) {
                        val user = child.getValue(UserModel::class.java)
                        if (user != null) {
                            userList.add(user)
                            checkPendingRequest(user.userId)
                        }
                    }
                    searchAdapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun sendFollowRequest(targetUserId: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val senderUser = snapshot.getValue(UserModel::class.java)
                if (senderUser != null) {
                    val requestRef = FirebaseDatabase.getInstance()
                        .getReference("follow_requests")
                        .child(targetUserId)
                        .child(currentUserId)

                    // Check if request already exists
                    requestRef.get().addOnSuccessListener { snapshot ->
                        if (snapshot.exists()) {
                            Toast.makeText(requireContext(), "Follow request already sent!", Toast.LENGTH_SHORT).show()
                        } else {
                            requestRef.setValue(senderUser).addOnSuccessListener {
                                pendingRequests[targetUserId] = true
                                searchAdapter.notifyDataSetChanged()
                                Toast.makeText(requireContext(), "Follow request sent!", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                Toast.makeText(requireContext(), "Failed to send request", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun saveToRecentSearches(username: String) {
        if (!recentSearchList.contains(username)) {
            recentSearchList.add(0, username)
            FirebaseDatabase.getInstance().getReference("recent_searches")
                .child(currentUserId).setValue(recentSearchList)
            recentAdapter.notifyDataSetChanged()
        }
    }

    private fun loadRecentSearches() {
        FirebaseDatabase.getInstance().getReference("recent_searches")
            .child(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    recentSearchList.clear()
                    for (child in snapshot.children) {
                        child.getValue(String::class.java)?.let { recentSearchList.add(it) }
                    }
                    recentAdapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun removeFromRecentSearches(username: String) {
        recentSearchList.remove(username)
        FirebaseDatabase.getInstance().getReference("recent_searches")
            .child(currentUserId).setValue(recentSearchList)
        recentAdapter.notifyDataSetChanged()
    }

    private fun checkPendingRequest(targetUserId: String) {
        val requestRef = FirebaseDatabase.getInstance()
            .getReference("follow_requests")
            .child(targetUserId)
            .child(currentUserId)

        requestRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                pendingRequests[targetUserId] = true
                searchAdapter.notifyDataSetChanged()
            }
        }
    }
}
