package com.example.foundit.ui.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foundit.R
import com.example.foundit.data.local.entities.Post
import com.example.foundit.databinding.ItemRecentBinding

class RecentPostAdapter(
    private var posts: List<Post>,
    private val onItemClick: (Post) -> Unit
) : RecyclerView.Adapter<RecentPostAdapter.HomePostViewHolder>() {

    inner class HomePostViewHolder(private val binding: ItemRecentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) = binding.apply {
            if (!post.imageUri.isNullOrEmpty()) {
                ivItemImage.setImageURI(Uri.parse(post.imageUri))
            } else {
                ivItemImage.setImageResource(R.drawable.ic_placeholder) // fallback
            }

            tvItemTitle.text = post.title
            tvItemLocation.text = post.location
            chipItemStatus.text = if (post.isFound) "Found" else "Lost"
            tvItemDate.text = post.date

            root.setOnClickListener { onItemClick(post) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePostViewHolder {
        val binding = ItemRecentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomePostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomePostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    fun updatePosts(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }
}
