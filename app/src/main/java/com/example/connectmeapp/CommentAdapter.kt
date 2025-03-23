package com.example.connectmeapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.connectmeapp.R
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream

class CommentAdapter(private val comments: List<CommentModel>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: CircleImageView = itemView.findViewById(R.id.commenter_profile)
        val username: TextView = itemView.findViewById(R.id.comment_username)
        val commentText: TextView = itemView.findViewById(R.id.comment_text)
        val timestamp: TextView = itemView.findViewById(R.id.comment_timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.username.text = comment.username
        holder.commentText.text = comment.text
        holder.timestamp.text = formatTimestamp(comment.timestamp)

        // Decode and set the profile image
        val bitmap = decodeBase64(comment.profileImageBase64)
        if (bitmap != null) {
            holder.profileImage.setImageBitmap(bitmap)
        } else {
            holder.profileImage.setImageResource(R.drawable.profile_placeholder) // Default image
        }
    }

    override fun getItemCount() = comments.size

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }

    // Function to decode Base64 string into Bitmap
    private fun decodeBase64(base64String: String?): Bitmap? {
        return try {
            if (base64String.isNullOrEmpty()) return null
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
