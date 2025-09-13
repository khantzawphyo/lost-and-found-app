package com.example.foundit.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foundit.R
import com.example.foundit.adapters.PostDiffCallback
import com.example.foundit.data.model.Post
import com.example.foundit.databinding.RecentPostBinding
import com.example.foundit.utils.ImageLoader

class RecentPostAdapter(
    private val onItemClick: (Post) -> Unit
) : ListAdapter<Post, RecentPostAdapter.RecentPostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentPostViewHolder {
        val binding = RecentPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentPostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post, onItemClick)
    }

    inner class RecentPostViewHolder(private val binding: RecentPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post, onItemClick: (Post) -> Unit) = binding.apply {
            // If imageUrl is null or blank, display the placeholder.
            // Otherwise, load the image from the URL.
            if (post.imageUrl.isNullOrBlank()) {
                ivItemImage.setImageResource(R.drawable.ic_placeholder)
            } else {
                ImageLoader.loadImage(ivItemImage, post.imageUrl)
            }

            tvItemTitle.text = post.title
            tvItemLocation.text = post.location
            tvItemStatus.text = if (post.found) "Found" else "Lost"
            val statusBackground = if (post.found) {
                R.drawable.bg_found_status_pill
            } else {
                R.drawable.bg_lost_status_pill
            }
            tvItemStatus.setBackgroundResource(statusBackground)
            root.setOnClickListener { onItemClick(post) }
        }
    }
}
