package com.example.connectmeapp

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StoryAdapter(private val stories: List<StoryModel>) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storyImage: ImageView = itemView.findViewById(R.id.story_image)
        val plusIcon: ImageView = itemView.findViewById(R.id.plus_icon)
        val storyBorder: ImageView = itemView.findViewById(R.id.story_border)
        val plusIconContainer: FrameLayout = itemView.findViewById(R.id.plus_icon_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.story_item, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = stories[position]
        holder.storyImage.setImageResource(story.imageRes)


        holder.storyImage.clipToOutline = true


        if (position == 0) {
            holder.plusIconContainer.visibility = View.VISIBLE
            holder.plusIcon.visibility = View.VISIBLE
            holder.storyBorder.visibility = View.GONE
        } else {
            holder.plusIconContainer.visibility = View.GONE
            holder.plusIcon.visibility = View.GONE
            holder.storyBorder.visibility = View.VISIBLE
        }
    }

    override fun getItemCount() = stories.size
}

