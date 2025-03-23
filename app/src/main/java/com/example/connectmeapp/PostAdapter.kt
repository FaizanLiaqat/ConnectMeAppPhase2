package com.example.connectmeapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class PostAdapter(
    private val posts: List<PostModel>
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.post_profile_image)
        val username: TextView = view.findViewById(R.id.post_username)
        val postImage: ImageView = view.findViewById(R.id.post_image)
        val likeButton: ImageView = view.findViewById(R.id.like_button)
        val commentButton: ImageView = view.findViewById(R.id.comment_button)
        val shareButton: ImageView = view.findViewById(R.id.share_button)
        val caption: TextView = view.findViewById(R.id.post_caption)
        val timestamp: TextView = view.findViewById(R.id.post_timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        // Set username and caption
        holder.username.text = post.username
        holder.caption.text = post.caption

        // Format timestamp
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        val formattedDate = sdf.format(Date(post.timestamp))
        holder.timestamp.text = formattedDate

        // Load profile image from Base64 string
        loadImage(holder.profileImage, post.profileImageUrl, R.drawable.profile_placeholder)

        // Load post image
        if (post.imageUrl.isNotEmpty()) {
            loadImage(holder.postImage, post.imageUrl, R.drawable.profile_placeholder)
            holder.postImage.visibility = View.VISIBLE
        } else {
            holder.postImage.visibility = View.GONE
        }

        //  Set the initial like button icon
        val isLiked = currentUserId in post.likes
        holder.likeButton.setImageResource(if (isLiked) R.drawable.red_heart_icon else R.drawable.heart_icon_v2)

//  Like button click listener (toggle like & update database)
        holder.likeButton.setOnClickListener {
            if (currentUserId == null) return@setOnClickListener
            val postRef = FirebaseDatabase.getInstance().getReference("posts").child(post.postId).child("likes")

            val updatedLikes = post.likes.toMutableMap() // Convert immutable Map to MutableMap

            if (isLiked) {
                updatedLikes.remove(currentUserId)
                postRef.child(currentUserId).removeValue().addOnSuccessListener {
                    holder.likeButton.setImageResource(R.drawable.heart_icon_v2)
                    Toast.makeText(holder.itemView.context, "Unliked", Toast.LENGTH_SHORT).show()
                }
            } else {
                updatedLikes[currentUserId] = true
                postRef.child(currentUserId).setValue(true).addOnSuccessListener {
                    holder.likeButton.setImageResource(R.drawable.red_heart_icon) // ️ Liked
                    Toast.makeText(holder.itemView.context, "Liked", Toast.LENGTH_SHORT).show()
                }
            }
        }


        // Comment button click
        holder.commentButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, CommentsActivity::class.java)
            intent.putExtra("POST_ID", post.postId)
            context.startActivity(intent)
        }

        // Share button click
        holder.shareButton.setOnClickListener {
            val context = holder.itemView.context
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this post by ${post.username}: ${post.caption}")
            context.startActivity(Intent.createChooser(shareIntent, "Share post via"))
        }
    }


    override fun getItemCount() = posts.size

    // Helper function to decode a Base64 string and set the image.
    private fun loadImage(imageView: ImageView, imageData: String, placeholder: Int) {
        if (imageData.isEmpty()) {
            imageView.setImageResource(placeholder)
        } else {
            try {
                val imageBytes = Base64.decode(imageData, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                imageView.setImageBitmap(bitmap)
            } catch (e: Exception) {
                imageView.setImageResource(placeholder)
            }
        }
    }
}
