package com.example.connectmeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class InviteAdapter(
    private val friends: List<UserModel>,
    private val onInviteClick: (UserModel) -> Unit
) : RecyclerView.Adapter<InviteAdapter.InviteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_invite, parent, false)
        return InviteViewHolder(view)
    }

    override fun onBindViewHolder(holder: InviteViewHolder, position: Int) {
        val friend = friends[position]
        holder.bind(friend)
    }

    override fun getItemCount(): Int = friends.size

    inner class InviteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: ImageView = itemView.findViewById(R.id.user_profile_image)
        private val username: TextView = itemView.findViewById(R.id.username)
        private val inviteButton: Button = itemView.findViewById(R.id.invite_button)

        fun bind(friend: UserModel) {
            profileImage.setImageResource(friend.profileImage)
            username.text = friend.username

            inviteButton.setOnClickListener {
                onInviteClick(friend)
            }
        }
    }
}