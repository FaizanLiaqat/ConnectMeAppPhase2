package com.example.connectmeapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ContactFragment : Fragment() {

    private lateinit var contactsRecyclerView: RecyclerView
    private lateinit var inviteRecyclerView: RecyclerView
    private lateinit var searchBar: EditText
    private lateinit var currentUsername: TextView
    private lateinit var backIcon: ImageView
    private lateinit var newMessageIcon: ImageView
    private val contactsList = mutableListOf<UserModel>()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)
        // Initialize UI components
        contactsRecyclerView = view.findViewById(R.id.contacts_recycler_view)
        inviteRecyclerView = view.findViewById(R.id.invite_recycler_view)
        searchBar = view.findViewById(R.id.search_bar)
        currentUsername = view.findViewById(R.id.current_username)
        backIcon = view.findViewById(R.id.back_icon)
        newMessageIcon = view.findViewById(R.id.new_message_icon)

        // Ideally, load the current username from Firebase or SharedPreferences
        currentUsername.setText("Your Username")

        // Back button
        backIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // New message button
        newMessageIcon.setOnClickListener {
            startActivity(Intent(requireContext(), NewMessageActivity::class.java))
        }

        // Setup RecyclerViews
        setupContactsRecyclerView()
        setupInviteRecyclerView()
        loadContacts()

        return view
    }

    private fun setupContactsRecyclerView() {
        // Optionally set fixed height as needed; here we simply set up layout manager and adapter
        contactsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        contactsRecyclerView.setHasFixedSize(true)
        contactsRecyclerView.adapter = ContactAdapter(contactsList) { contact ->
            // Open ChatBoxActivity with contact info
            val intent = Intent(requireContext(), ChatBoxActivity::class.java).apply {
                putExtra("username", contact.username)
                putExtra("profileImage", contact.profileImageUrl)
                putExtra("otherUserId", contact.userId)
            }
            startActivity(intent)
        }
    }

    private fun setupInviteRecyclerView() {
        inviteRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        inviteRecyclerView.setHasFixedSize(true)
        inviteRecyclerView.adapter = InviteAdapter(getDummyInviteList()) { friend ->
            Toast.makeText(requireContext(), "Invited ${friend.username} to connect", Toast.LENGTH_SHORT).show()
        }
    }

    // Load all users (contacts) from Firebase except the current user.
    private fun loadContacts() {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                contactsList.clear()
                for (child in snapshot.children) {
                    val user = child.getValue(UserModel::class.java)
                    if (user != null && user.userId != currentUserId) {
                        contactsList.add(user)
                    }
                }
                contactsRecyclerView.adapter?.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error loading contacts", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Dummy invite list remains for now
    private fun getDummyInviteList(): List<UserModel> {
        return listOf(
            UserModel("id1", "Frank", ""),
            UserModel("id2", "Grace", ""),
            UserModel("id3", "Hank", ""),
            UserModel("id4", "Ivy", ""),
            UserModel("id5", "Jack", ""),
            UserModel("id6", "Kate", ""),
            UserModel("id7", "Liam", ""),
            UserModel("id8", "Mia", "")
        )
    }
}
