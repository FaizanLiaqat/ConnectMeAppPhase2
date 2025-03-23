package com.example.connectmeapp

data class ChatModel(
    val message: String = "",
    val senderId: String = "",
    val timestamp: String = "", // Formatted timestamp string (if needed)
    val timestampLong: Long = 0L // Raw timestamp for sorting
)
