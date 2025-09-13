package com.example.foundit.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foundit.R
import com.example.foundit.adapters.PostDiffCallback
import com.example.foundit.data.model.Post
import com.example.foundit.data.repository.AuthRepository
import com.example.foundit.databinding.ItemPostBinding
import com.example.foundit.utils.ImageLoader
import com.example.foundit.utils.formatTimestamp

class PostAdapter(
    private val onItemClick: (Post) -> Unit,
    private val onEditClick: (Post) -> Unit,
    private val onDeleteClick: (Post) -> Unit,
    private val isMyPostsAdapter: Boolean = false
) : ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post, onItemClick, onEditClick, onDeleteClick)
    }

    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            post: Post,
            onItemClick: (Post) -> Unit,
            onEditClick: (Post) -> Unit,
            onDeleteClick: (Post) -> Unit
        ) = binding.apply {
            tvUsername.text = post.postedBy
            tvItemDate.text = formatTimestamp(post.timestamp)
            tvItemTitle.text = post.title
            tvItemDescription.text = post.description

            if (!post.location.isNullOrBlank()) {
                tvItemLocation.text = post.location
                tvItemLocation.visibility = View.VISIBLE
                ivLocationIcon.visibility = View.VISIBLE
            } else {
                tvItemLocation.visibility = View.GONE
                ivLocationIcon.visibility = View.GONE
            }

            if (!post.imageUrl.isNullOrBlank()) {
                ivItemImage.visibility = View.VISIBLE
                ImageLoader.loadImage(ivItemImage, post.imageUrl)
            } else {
                ivItemImage.visibility = View.GONE
            }

            ivPosterProfile.setImageResource(R.drawable.avatar)
            tvItemStatus.text = if (post.found) "Found" else "Lost"
            val statusBackground = if (post.found) {
                R.drawable.bg_found_status_pill
            } else {
                R.drawable.bg_lost_status_pill
            }
            tvItemStatus.setBackgroundResource(statusBackground)

            val currentUserId = AuthRepository.getCurrentUserId()

            if (isMyPostsAdapter && currentUserId == post.userId) {
                ivPostMenu.visibility = View.VISIBLE
                ivPostMenu.setOnClickListener {
                    showPopupMenu(ivPostMenu, post, onEditClick, onDeleteClick)
                }
            } else {
                ivPostMenu.visibility = View.GONE
            }

            root.setOnClickListener { onItemClick(post) }
        }

        private fun showPopupMenu(
            view: View,
            post: Post,
            onEditClick: (Post) -> Unit,
            onDeleteClick: (Post) -> Unit
        ) {
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.post_menu, popup.menu)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        onEditClick(post)
                        true
                    }
                    R.id.action_delete -> {
                        onDeleteClick(post)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }
}
