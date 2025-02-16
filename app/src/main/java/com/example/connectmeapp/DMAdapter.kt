package com.example.connectmeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DMAdapter(private val users: List<UserModel>, private val onItemClick: (UserModel) -> Unit)
    : RecyclerView.Adapter<DMAdapter.DMViewHolder>() {

    class DMViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.user_profile_image)
        val username: TextView = itemView.findViewById(R.id.username)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DMViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dm, parent, false)
        return DMViewHolder(view)
    }

    override fun onBindViewHolder(holder: DMViewHolder, position: Int) {
        val user = users[position]
        holder.profileImage.setImageResource(user.profileImage)
        holder.username.text = user.username
        holder.profileImage.clipToOutline = true
        // ✅ Handle Click Event
        holder.itemView.setOnClickListener {
            onItemClick(user)  // Calls openChatBox() from Fragment
        }
    }

    override fun getItemCount() = users.size
}
