package com.example.connectmeapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class SearchAdapter(
    private val userList: List<UserModel>,
    private val onUserClick: (UserModel) -> Unit,
    private val onFollowClick: (UserModel) -> Unit,
    private val pendingRequests: MutableMap<String, Boolean> // Track pending follow requests
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userImage: ImageView = view.findViewById(R.id.user_image)
        val userName: TextView = view.findViewById(R.id.user_name)
        val followButton: Button = view.findViewById(R.id.follow_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val user = userList[position]
        holder.userName.text = user.username

        // Load profile image if available (assuming it's stored in Base64 format)
        if (user.profileImageUrl.isNotEmpty()) {
            val bitmap = decodeBase64ToBitmap(user.profileImageUrl)
            holder.userImage.setImageBitmap(bitmap)
        }

        // Check if the user already has a pending follow request
        val isRequestSent = pendingRequests[user.userId] == true

        // Update follow button text based on request status
        holder.followButton.text = if (isRequestSent) "Request Sent" else "Follow"
        holder.followButton.isEnabled = !isRequestSent // Disable if request already sent

        holder.itemView.setOnClickListener { onUserClick(user) }

        holder.followButton.setOnClickListener {
            if (!isRequestSent) {
                onFollowClick(user) // Send request
                pendingRequests[user.userId] = true // Update state
                notifyItemChanged(position) // Refresh UI
                Toast.makeText(holder.itemView.context, "Follow request sent!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(holder.itemView.context, "Follow request already sent!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = userList.size

    private fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}

