package com.example.connectmeapp

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private val contacts: List<UserModel>,
    private val onMessageClick: (UserModel) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.bind(contact)
    }

    override fun getItemCount(): Int = contacts.size

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: ImageView = itemView.findViewById(R.id.user_profile_image)
        private val username: TextView = itemView.findViewById(R.id.username)
        private val messageIcon: ImageView = itemView.findViewById(R.id.message_icon)

        fun bind(contact: UserModel) {
            // Load profile image from Base64 (or set placeholder)
            if (contact.profileImageUrl.isNotEmpty()) {
                try {
                    val imageBytes = Base64.decode(contact.profileImageUrl, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    profileImage.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    profileImage.setImageResource(R.drawable.profile_placeholder)
                }
            } else {
                profileImage.setImageResource(R.drawable.profile_placeholder)
            }
            username.text = contact.username

            // Handle message icon click
            messageIcon.setOnClickListener {
                onMessageClick(contact)
            }
        }
    }
}
