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
import androidx.recyclerview.widget.RecyclerView

class RequestAdapter(
    private var requestList: List<UserModel>,
    private val onRequestAction: (UserModel, Boolean) -> Unit
) : RecyclerView.Adapter<RequestAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.user_profile_image)
        val usernameText: TextView = view.findViewById(R.id.usernameText)
        val acceptButton: Button = view.findViewById(R.id.acceptButton)
        val declineButton: Button = view.findViewById(R.id.declineButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_request, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = requestList[position]
        holder.usernameText.text = user.username

        if (user.profileImageUrl.isNotEmpty()) {
            try {
                val decodedBytes = Base64.decode(user.profileImageUrl, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                holder.profileImage.setImageBitmap(bitmap)
            } catch (e: Exception) {
                holder.profileImage.setImageResource(R.drawable.profile_placeholder)
            }
        } else {
            holder.profileImage.setImageResource(R.drawable.profile_placeholder)
        }

        holder.acceptButton.setOnClickListener { onRequestAction(user, true) }
        holder.declineButton.setOnClickListener { onRequestAction(user, false) }
    }

    override fun getItemCount(): Int = requestList.size
}
