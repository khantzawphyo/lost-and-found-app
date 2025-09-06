package com.example.foundit.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foundit.R
import com.example.foundit.adapters.PostDiffCallback
import com.example.foundit.data.model.Post
import com.example.foundit.databinding.RecentPostBinding

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
            // Using a placeholder for now
            ivItemImage.setImageResource(R.drawable.ic_placeholder)

            tvItemTitle.text = post.title
            tvItemLocation.text = post.location
            chipItemStatus.text = if (post.isFound) "Found" else "Lost"
            tvItemDate.text = post.date

            root.setOnClickListener { onItemClick(post) }
        }
    }
}