package com.example.connectmeapp

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatBoxActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageInput: EditText
    private lateinit var sendIcon: ImageView

    private val messages = mutableListOf<ChatModel>()
    private val currentUserId = "123"  // Temporary unique ID for testing
    private val otherUserId = "456" // Another ID for the recipient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_box)

        // 🔹 Initialize UI Elements
        chatRecyclerView = findViewById(R.id.chat_recycler_view)
        messageInput = findViewById(R.id.message_input)
        sendIcon = findViewById(R.id.send_icon)

        // 🔹 Initialize Adapter Before Adding Messages
        chatAdapter = ChatAdapter(messages, currentUserId)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        // ✅ Now Safe to Add Dummy Messages
        addDummyMessages()

        sendIcon.setOnClickListener {
            sendMessage()
        }
    }

    // ✅ Function to Add Dummy Messages
    private fun addDummyMessages() {
        messages.add(ChatModel("Hey! How are you?", otherUserId, "12:30 PM"))
        messages.add(ChatModel("I'm good, what about you?", currentUserId, "12:31 PM"))
        messages.add(ChatModel("I'm fine too. What are you up to?", otherUserId, "12:32 PM"))
        messages.add(ChatModel("Just working on some Android projects. 🚀", currentUserId, "12:33 PM"))
        messages.add(ChatModel("That's awesome! Keep it up! 💪", otherUserId, "12:34 PM"))

        // ✅ Notify Adapter After Adding Messages
        chatAdapter.notifyDataSetChanged()
        chatRecyclerView.scrollToPosition(messages.size - 1)
    }

    // ✅ Function to Send Messages
    private fun sendMessage() {
        val messageText = messageInput.text.toString().trim()
        if (messageText.isNotEmpty()) {
            val message = ChatModel(messageText, currentUserId, "12:45 PM")
            messages.add(message)
            chatAdapter.notifyItemInserted(messages.size - 1)
            chatRecyclerView.scrollToPosition(messages.size - 1)
            messageInput.setText("")
        }
    }
}




