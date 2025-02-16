package com.example.connectmeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeAdapter(private val items: List<HomeItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
                val view = LayoutInflater.from(parent.context).inflate(R.layout.fourth_screen_story_layer, parent, false)
                StoryViewHolder(view)
            }
            VIEW_TYPE_POST -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
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

    //  ViewHolder for Stories
    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val storiesRecyclerView: RecyclerView = itemView.findViewById(R.id.stories_recycler_view)

        fun bind(stories: List<StoryModel>) {
            storiesRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            storiesRecyclerView.adapter = StoryAdapter(stories)
        }
    }

    //  ViewHolder for Posts (Now Includes bind Method)
    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: ImageView = itemView.findViewById(R.id.post_profile_image)
        private val username: TextView = itemView.findViewById(R.id.post_username)
        private val postImage: ImageView = itemView.findViewById(R.id.post_image)
        private val caption: TextView = itemView.findViewById(R.id.post_caption)

        fun bind(post: PostModel) {
            profileImage.setImageResource(post.profileImage)
            profileImage.clipToOutline = true
            username.text = post.username
            postImage.setImageResource(post.postImage)
            caption.text = post.caption
        }
    }
}

