package com.example.connectmeapp

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class GalleryAdapter(
    private val images: List<Uri>,
    private val onImageSelected: (Uri) -> Unit
) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {
    private var selectedPosition = 0 // Default to first image

    // Method to get the currently selected image URI
    fun getSelectedImageUri(): Uri {
        return if (images.isNotEmpty()) {
            images[selectedPosition]
        } else {
            // Return a default URI or handle empty list scenario
            Uri.EMPTY
        }
    }

    class GalleryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.gallery_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gallery, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val uri = images[position]
        holder.imageView.setImageURI(uri)

        // Set aspect ratio for the image view (1:1)
        holder.imageView.post {
            val width = holder.imageView.width
            val layoutParams = holder.imageView.layoutParams
            layoutParams.height = width
            holder.imageView.layoutParams = layoutParams
        }

        // Highlight the selected image
        holder.imageView.alpha = if (position == selectedPosition) 1.0f else 0.7f

        holder.itemView.setOnClickListener {
            // Get current position when clicked
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                // Update selected position
                val previousSelected = selectedPosition
                selectedPosition = currentPosition

                // Update UI for previous and new selection
                notifyItemChanged(previousSelected)
                notifyItemChanged(selectedPosition)

                onImageSelected(uri)
            }
        }
    }

    override fun getItemCount() = images.size
}