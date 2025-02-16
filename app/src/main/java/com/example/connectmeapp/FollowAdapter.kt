package com.example.connectmeapp
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FollowAdapter(
    private val users: List<UserModel>,
    private val onItemClick: (UserModel) -> Unit
) : RecyclerView.Adapter<FollowAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.user_profile_image)
        val username: TextView = itemView.findViewById(R.id.username)
        val messageIcon: ImageView = itemView.findViewById(R.id.message_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_follow, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.profileImage.setImageResource(user.profileImage)
        holder.username.text = user.username

        // ✅ Handle Click on Item
        holder.itemView.setOnClickListener {
            onItemClick(user)
        }

        // ✅ Handle Click on Message Icon (Optional)
        holder.messageIcon.setOnClickListener {
            onItemClick(user)
        }
    }

    override fun getItemCount() = users.size
}


