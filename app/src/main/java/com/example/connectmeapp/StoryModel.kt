package com.example.connectmeapp

data class StoryModel(
    val id: String = "",               // Firebase unique ID
    val userId: String = "",           // User who posted the story
    val imageBase64: String = "",      // Image stored as Base64 string
    val timestamp: Long = 0,           // When the story was posted
    val username: String = "",         // Username for display
    val userProfileImageUrl: String = "", // Profile image URL or placeholder
    val viewedBy: Map<String, Boolean> = emptyMap() // Users who viewed this story
) {
    // Calculate if story is still valid (less than 24 hours old)
    fun isValid(): Boolean {
        val currentTime = System.currentTimeMillis()
        val storyAge = currentTime - timestamp
        val oneDayInMillis = 24 * 60 * 60 * 1000
        return storyAge < oneDayInMillis
    }
}
