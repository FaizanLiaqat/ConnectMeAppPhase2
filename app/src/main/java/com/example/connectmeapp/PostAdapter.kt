package com.example.connectmeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private val posts: List<PostModel>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.post_profile_image)
        val username: TextView = itemView.findViewById(R.id.post_username)
        val postImage: ImageView = itemView.findViewById(R.id.post_image)
        val caption: TextView = itemView.findViewById(R.id.post_caption)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        // ✅ Load data dynamically into the XML
        holder.profileImage.setImageResource(post.profileImage)  // Profile Picture
        holder.profileImage.clipToOutline = true
        holder.username.text = post.username                    // Username
        holder.postImage.setImageResource(post.postImage)       // Post Image
        holder.caption.text = post.caption                      // Post Caption
    }

    override fun getItemCount() = posts.size
}