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
class ContactFragment : Fragment() {
    private lateinit var contactsRecyclerView: RecyclerView
    private lateinit var inviteRecyclerView: RecyclerView
    private lateinit var searchBar: EditText
    private lateinit var currentUsername: TextView
    private lateinit var backIcon: ImageView
    private lateinit var newMessageIcon: ImageView
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

        // Set username (you might want to get this from a shared preference or intent)
        currentUsername.text = "Your Username" // Replace with actual username

        // Set up back button
        backIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Set up new message button
        newMessageIcon.setOnClickListener {
            startActivity(Intent(requireContext(), NewMessageActivity::class.java))
        }

        // Set up contacts recycler view
        setupContactsRecyclerView()

        // Set up invite recycler view
        setupInviteRecyclerView()

        return view
    }

    private fun setupContactsRecyclerView() {
        // Set fixed height programmatically (200dp converted to pixels)
        val heightInPixels = (200 * resources.displayMetrics.density).toInt()
        val params = contactsRecyclerView.layoutParams
        params.height = heightInPixels
        contactsRecyclerView.layoutParams = params

        // Set to false in layout XML but handle scrolling within the RecyclerView
        contactsRecyclerView.isNestedScrollingEnabled = false

        contactsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        contactsRecyclerView.setHasFixedSize(true)

        // Create adapter with message click listener
        val contactAdapter = ContactAdapter(getContactsList()) { contact ->
            // Handle message click - open chat with this contact
            val intent = Intent(requireContext(), ChatBoxActivity::class.java)
            intent.putExtra("username", contact.username)
            intent.putExtra("profileImage", contact.profileImage)
            startActivity(intent)
        }

        contactsRecyclerView.adapter = contactAdapter
    }

    private fun setupInviteRecyclerView() {
        // Set fixed height programmatically (200dp converted to pixels)
        val heightInPixels = (200 * resources.displayMetrics.density).toInt()
        val params = inviteRecyclerView.layoutParams
        params.height = heightInPixels
        inviteRecyclerView.layoutParams = params

        // Set to false in layout XML but handle scrolling within the RecyclerView
        inviteRecyclerView.isNestedScrollingEnabled = false

        inviteRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        inviteRecyclerView.setHasFixedSize(true)

        // Create adapter with invite click listener
        val inviteAdapter = InviteAdapter(getInviteFriendsList()) { friend ->
            // Handle invite click
            Toast.makeText(
                requireContext(),
                "Invited ${friend.username} to connect",
                Toast.LENGTH_SHORT
            ).show()
        }

        inviteRecyclerView.adapter = inviteAdapter
    }

    // Dummy data for contacts list
    private fun getContactsList(): List<UserModel> {
        return listOf(
            UserModel(R.drawable.profile_placeholder, "Alice"),
            UserModel(R.drawable.profile_placeholder, "Bob"),
            UserModel(R.drawable.profile_placeholder, "Charlie"),
            UserModel(R.drawable.profile_placeholder, "David"),
            UserModel(R.drawable.profile_placeholder, "Emma"),
            UserModel(R.drawable.profile_placeholder, "Frank"),
            UserModel(R.drawable.profile_placeholder, "Grace"),
            UserModel(R.drawable.profile_placeholder, "Helen"),
            UserModel(R.drawable.profile_placeholder, "Ian")
        )
    }

    // Dummy data for invite friends list
    private fun getInviteFriendsList(): List<UserModel> {
        return listOf(
            UserModel(R.drawable.profile_placeholder, "Frank"),
            UserModel(R.drawable.profile_placeholder, "Grace"),
            UserModel(R.drawable.profile_placeholder, "Hank"),
            UserModel(R.drawable.profile_placeholder, "Ivy"),
            UserModel(R.drawable.profile_placeholder, "Jack"),
            UserModel(R.drawable.profile_placeholder, "Kate"),
            UserModel(R.drawable.profile_placeholder, "Liam"),
            UserModel(R.drawable.profile_placeholder, "Mia")
        )
    }
}