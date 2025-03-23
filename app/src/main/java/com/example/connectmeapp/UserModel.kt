package com.example.connectmeapp

data class UserModel(
    val userId: String = "",
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val bio: String = "",
    val profileImageUrl: String = "",
    val createdAt: Long = 0,
    val isNewUser: Boolean = true
) {
    // Constructor for dummy data in FollowAdapter
    constructor(profileImage: Int, username: String) : this(
        userId = "",
        name = "",
        username = username,
        email = "",
        phoneNumber = "",
        bio = "",
        profileImageUrl = "",
        createdAt = 0,
        isNewUser = false
    )
}