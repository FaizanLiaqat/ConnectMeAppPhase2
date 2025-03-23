package com.example.connectmeapp

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class HomeAdapter(
    private val items: List<HomeItem>,
    private val currentUserId: String,
    private val onStoryClick: (StoryModel) -> Unit,
    private val onAddStoryClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_STORY = 0
        private const val VIEW_TYPE_POST = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HomeItem.StoryList -> VIEW_TYPE_STORY
            is HomeItem.PostItem -> VIEW_TYPE_POST
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_STORY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.fourth_screen_story_layer, parent, false)
                StoryViewHolder(view)
            }
            VIEW_TYPE_POST -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.post_item, parent, false)
                PostViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is HomeItem.StoryList -> (holder as StoryViewHolder).bind(item.stories)
            is HomeItem.PostItem -> (holder as PostViewHolder).bind(item.post)
        }
    }

    override fun getItemCount() = items.size

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val storiesRecyclerView: RecyclerView = itemView.findViewById(R.id.stories_recycler_view)

        fun bind(stories: List<StoryModel>) {
            storiesRecyclerView.layoutManager = LinearLayoutManager(
                itemView.context, LinearLayoutManager.HORIZONTAL, false
            )
            storiesRecyclerView.adapter = StoryAdapter(
                stories,
                currentUserId,
                onStoryClick,
                onAddStoryClick
            )
        }
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImageView: ImageView = itemView.findViewById(R.id.post_profile_image)
        private val usernameText: TextView = itemView.findViewById(R.id.post_username)
        private val postImageView: ImageView = itemView.findViewById(R.id.post_image)
        private val captionText: TextView = itemView.findViewById(R.id.post_caption)

        fun bind(post: PostModel) {
            loadImage(profileImageView, post.profileImageUrl)
            usernameText.text = post.username
            loadImage(postImageView, post.imageUrl)
            captionText.text = post.caption
        }

        // Helper function to load an image into an ImageView.
        // It first tries to convert the imageData to an integer resource ID.
        // If that fails, it attempts to decode it as a Base64 string.
        private fun loadImage(imageView: ImageView, imageData: String) {
            if (imageData.isEmpty()) {
                imageView.setImageResource(R.drawable.profile_placeholder)
            } else {
                try {
                    // Try to interpret the string as a resource ID (dummy data scenario)
                    val resId = imageData.toInt()
                    imageView.setImageResource(resId)
                } catch (e: NumberFormatException) {
                    // If not a resource ID, try to decode it as Base64.
                    try {
                        val imageBytes = Base64.decode(imageData, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        imageView.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        imageView.setImageResource(R.drawable.profile_placeholder)
                    }
                }
            }
        }
    }
}
