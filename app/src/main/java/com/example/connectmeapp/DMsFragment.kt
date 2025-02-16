package com.example.connectmeapp

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView



class DMsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dms, container, false)

        // 🔹 Search Bar & RecyclerView
        val searchBar = view.findViewById<EditText>(R.id.search_dm)
        val recyclerView = view.findViewById<RecyclerView>(R.id.dm_recycler_view)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // ✅ Pass a Click Listener to Adapter
        val adapter = DMAdapter(getSampleUsers()) { user ->
            openChatBox(user)
        }

        recyclerView.adapter = adapter

        return view
    }

    // ✅ Generate Placeholder Users (Faster Testing)
    private fun getSampleUsers(): List<UserModel> {
        return listOf(
            UserModel(R.drawable.profile_placeholder, "Alice"),
            UserModel(R.drawable.profile_placeholder, "Bob"),
            UserModel(R.drawable.profile_placeholder, "Charlie"),
            UserModel(R.drawable.profile_placeholder, "David"),
            UserModel(R.drawable.profile_placeholder, "Eve")
        )
    }

    // ✅ Function to Open ChatBoxActivity
    private fun openChatBox(user: UserModel) {
        val intent = Intent(requireContext(), ChatBoxActivity::class.java).apply {
            putExtra("username", user.username)
            putExtra("profileImage", user.profileImage)
        }

        // ✅ Use ActivityOptions for Smooth Transition Animation
        val options = ActivityOptions.makeCustomAnimation(
            requireContext(),
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        startActivity(intent, options.toBundle())
    }
}

//class DMsFragment : Fragment() {
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val view = inflater.inflate(R.layout.fragment_dms, container, false)
//
//        // 🔹 Search Bar & RecyclerView
//        val searchBar = view.findViewById<EditText>(R.id.search_dm)
//        val recyclerView = view.findViewById<RecyclerView>(R.id.dm_recycler_view)
//
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//
//        // ✅ Pass a Click Listener to Adapter
//        val adapter = DMAdapter(getSampleUsers()) {
//            user -> openChatBox(user)
//        }
//
//        recyclerView.adapter = adapter
//
//        return view
//    }
//
//    private fun getSampleUsers(): List<UserModel> {
//        return listOf(
//            UserModel(R.drawable.user1, "Alice"),
//            UserModel(R.drawable.user2, "Bob"),
//            UserModel(R.drawable.user3, "Charlie")
//        )
//    }
//
//    // ✅ Function to Open ChatBoxActivity
//    private fun openChatBox(user: UserModel) {
//        val intent = Intent(requireContext(), ChatBoxActivity::class.java).apply {
//            putExtra("username", user.username)
//            putExtra("profileImage", user.profileImage)
//        }
//
//        // Use ActivityOptions for animation (New API)
//        val options = ActivityOptions.makeCustomAnimation(
//            requireContext(),
//            R.anim.slide_in_right,
//            R.anim.slide_out_left
//        )
//        startActivity(intent, options.toBundle())
//    }
//
//
//}

