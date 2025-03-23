package com.example.connectmeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecentSearchAdapter(
    private val recentList: MutableList<String>,
    private val onRemoveClick: (String) -> Unit
) : RecyclerView.Adapter<RecentSearchAdapter.RecentViewHolder>() {

    class RecentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recentUsername: TextView = view.findViewById(R.id.recent_username)
        val removeRecent: ImageView = view.findViewById(R.id.remove_recent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent, parent, false)
        return RecentViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        val username = recentList[position]
        holder.recentUsername.text = username
        holder.removeRecent.setOnClickListener {
            onRemoveClick(username)
            recentList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount() = recentList.size
}
