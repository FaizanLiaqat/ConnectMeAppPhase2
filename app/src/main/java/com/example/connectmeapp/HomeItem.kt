package com.example.connectmeapp

sealed class HomeItem {
    data class StoryList(val stories: List<StoryModel>) : HomeItem()
    data class PostItem(val post: PostModel) : HomeItem()
}
