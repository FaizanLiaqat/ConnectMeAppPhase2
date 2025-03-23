package com.example.connectmeapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatBoxActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageInput: EditText
    private lateinit var sendIcon: ImageView

    private val messages = mutableListOf<ChatModel>()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "123"  // Use real UID from FirebaseAuth
    private val otherUserId = intent.getStringExtra("otherUserId") ?: "456" // Pass this via intent from DM list/profile

    private lateinit var chatRef: DatabaseReference
    private lateinit var conversationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_box)

        // Initialize UI elements
        chatRecyclerView = findViewById(R.id.chat_recycler_view)
        messageInput = findViewById(R.id.message_input)
        sendIcon = findViewById(R.id.send_icon)

        // Initialize call buttons
        findViewById<ImageView>(R.id.phone_icon).setOnClickListener {
            // Launch PhoneCallActivity
            val username = intent.getStringExtra("username") ?: "Unknown"
            val profileImage = intent.getIntExtra("profileImage", R.drawable.profile_placeholder)
            val intent = Intent(this, PhoneCallActivity::class.java).apply {
                putExtra("username", username)
                putExtra("profileImage", profileImage)
            }
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.video_call_icon).setOnClickListener {
            // Launch VideoCallActivity (placeholder for Agora integration)
            val username = intent.getStringExtra("username") ?: "Unknown"
            val profileImage = intent.getIntExtra("profileImage", R.drawable.profile_placeholder)
            val intent = Intent(this, VideoCallActivity::class.java).apply {
                putExtra("username", username)
                putExtra("profileImage", profileImage)
            }
            startActivity(intent)
        }

        // Compute a unique conversation ID by sorting the two UIDs lexicographically.
        conversationId = if (currentUserId < otherUserId)
            "${currentUserId}_${otherUserId}"
        else
            "${otherUserId}_${currentUserId}"
        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(conversationId)


        // Initialize adapter and RecyclerView
        chatAdapter = ChatAdapter(messages, currentUserId)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        loadMessages()

        sendIcon.setOnClickListener {
            sendMessage()
        }
    }

    private fun loadMessages() {
        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messages.clear()
                for (child in snapshot.children) {
                    val chat = child.getValue(ChatModel::class.java)
                    if (chat != null) {
                        messages.add(chat)
                    }
                }
                // Sort messages by timestamp (newest at the bottom)
                messages.sortBy { it.timestampLong }
                chatAdapter.notifyDataSetChanged()
                chatRecyclerView.scrollToPosition(messages.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatBoxActivity, "Error loading messages: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendMessage() {
        val messageText = messageInput.text.toString().trim()
        if (messageText.isNotEmpty()) {
            val timestamp = System.currentTimeMillis()
            // Optionally, format the timestamp as desired (e.g., "12:45 PM")
            val formattedTimestamp = android.text.format.DateFormat.format("hh:mm a", timestamp).toString()
            val chat = ChatModel(messageText, currentUserId, formattedTimestamp, timestamp)
            chatRef.push().setValue(chat)
                .addOnSuccessListener {
                    messageInput.setText("")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error sending message: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
