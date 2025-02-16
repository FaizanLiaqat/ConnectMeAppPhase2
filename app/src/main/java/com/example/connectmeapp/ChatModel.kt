package com.example.connectmeapp

data class ChatModel(
    val message: String,
    val senderId: String,  // Unique ID for sender
    val timestamp: String
)
