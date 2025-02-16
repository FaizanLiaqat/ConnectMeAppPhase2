package com.example.connectmeapp
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FollowersFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: EditText
    private lateinit var adapter: FollowAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_followers, container, false)

        recyclerView = view.findViewById(R.id.follow_recycler_view)
        searchBar = view.findViewById(R.id.search_bar)

        // ✅ Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        // ✅ Pass the Click Listener to Adapter
        adapter = FollowAdapter(getDummyFollowers()) { user ->
            val intent = Intent(requireContext(), ChatBoxActivity::class.java)
            intent.putExtra("username", user.username)
            intent.putExtra("profileImage", user.profileImage)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        return view
    }

    // ✅ Dummy Followers Data
    private fun getDummyFollowers(): List<UserModel> {
        return listOf(
            UserModel(R.drawable.profile_placeholder, "Alice"),
            UserModel(R.drawable.profile_placeholder, "Bob"),
            UserModel(R.drawable.profile_placeholder, "Charlie"),
            UserModel(R.drawable.profile_placeholder, "David"),
            UserModel(R.drawable.profile_placeholder, "Emma"),
            UserModel(R.drawable.profile_placeholder, "Sophia"),
            UserModel(R.drawable.profile_placeholder, "Liam"),
            UserModel(R.drawable.profile_placeholder, "Olivia"),
            UserModel(R.drawable.profile_placeholder, "Noah"),
            UserModel(R.drawable.profile_placeholder, "Ava")
        )
    }
}

