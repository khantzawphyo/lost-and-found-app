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

class PostAdapter(
    private val onItemClick: (Post) -> Unit,
    private val onEditClick: (Post) -> Unit,
    private val onDeleteClick: (Post) -> Unit,
    // if the user is in my posts, show the menu, if not, don't
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
            tvItemDate.text = post.date
            tvItemTitle.text = post.title
            tvItemDescription.text = post.description

            // Using a placeholder for now
            ivItemImage.setImageResource(R.drawable.ic_placeholder)
            ivPosterProfile.setImageResource(R.drawable.avatar)
            chipItemStatus.text = if (post.isFound) "Found" else "Lost"

            // Get the current user ID
            val currentUserId = AuthRepository.getCurrentUserId()

            // Conditionally show the menu icon based on user ID and the adapter type
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
