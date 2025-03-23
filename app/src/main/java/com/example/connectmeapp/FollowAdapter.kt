package com.example.connectmeapp

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class FollowAdapter(
    private val users: List<UserModel>,
    private val currentUserId: String,
    private val onItemClick: (UserModel) -> Unit,
    private val onFollowClick: (UserModel, Boolean) -> Unit
) : RecyclerView.Adapter<FollowAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.user_profile_image)
        val username: TextView = itemView.findViewById(R.id.username)
        val messageIcon: ImageView = itemView.findViewById(R.id.message_icon)
        val followButton: Button = itemView.findViewById(R.id.follow_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_follow, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        // Load profile image from Base64 if available; otherwise, use a placeholder.
        if (user.profileImageUrl.isNotEmpty()) {
            try {
                val imageBytes = Base64.decode(user.profileImageUrl, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.profileImage.setImageBitmap(decodedImage)
            } catch (e: Exception) {
                holder.profileImage.setImageResource(R.drawable.profile_placeholder)
            }
        } else {
            holder.profileImage.setImageResource(R.drawable.profile_placeholder)
        }
        holder.username.text = user.username

        // Set click listeners for the entire item (to open a chat or profile)
        holder.itemView.setOnClickListener { onItemClick(user) }
        holder.messageIcon.setOnClickListener { onItemClick(user) }

        // Check if the current user follows this user.
        val followRef = FirebaseDatabase.getInstance()
            .getReference("following")
            .child(currentUserId)
            .child(user.userId)

        followRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isFollowing = snapshot.exists()
                holder.followButton.text = if (isFollowing) "Following" else "Follow"
                // Set click listener to toggle follow/unfollow.
                holder.followButton.setOnClickListener {
                    onFollowClick(user, isFollowing)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed.
            }
        })
    }

    override fun getItemCount() = users.size
}
