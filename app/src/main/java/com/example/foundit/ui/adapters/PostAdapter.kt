package com.example.foundit.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foundit.R
import com.example.foundit.adapters.PostDiffCallback
import com.example.foundit.data.model.Post
import com.example.foundit.databinding.PostBinding

class PostAdapter(
    private val onItemClick: (Post) -> Unit
) : ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = PostBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post, onItemClick)
    }

    inner class PostViewHolder(private val binding: PostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post, onItemClick: (Post) -> Unit) = binding.apply {
            tvUsername.text = post.postedBy
            tvItemDate.text = post.date
            tvItemTitle.text = post.title
            tvItemDescription.text = post.description

            // Using a placeholder for now
            ivItemImage.setImageResource(R.drawable.ic_placeholder)

            ivPosterProfile.setImageResource(R.drawable.avatar)
            chipItemStatus.text = if (post.isFound) "Found" else "Lost"

            root.setOnClickListener { onItemClick(post) }
        }
    }
}