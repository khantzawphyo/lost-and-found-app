package com.example.foundit.ui.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foundit.R
import com.example.foundit.data.local.entities.Post
import com.example.foundit.databinding.ItemPostBinding

class PostAdapter(
    private var posts: List<Post>,
    private val onItemClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) = binding.apply {
            tvUsername.text = post.postedBy
            tvItemDate.text = post.date
            tvItemTitle.text = post.title
            tvItemDescription.text = post.description

            if (!post.imageUri.isNullOrEmpty()) {
                ivItemImage.setImageURI(Uri.parse(post.imageUri))
            } else {
                ivItemImage.setImageResource(R.drawable.ic_placeholder)
            }

            ivPosterProfile.setImageResource(R.drawable.avatar)
            chipItemStatus.text = if (post.isFound) "Found" else "Lost"

            root.setOnClickListener { onItemClick(post) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding =
            ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    fun updatePosts(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }
}
