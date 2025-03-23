package com.example.connectmeapp

data class CommentModel(
    val commentId: String = "",
    val userId: String = "",
    val username: String = "",
    val profileImageBase64: String = "", // Base64 image string
    val text: String = "",
    val timestamp: Long = 0
)
