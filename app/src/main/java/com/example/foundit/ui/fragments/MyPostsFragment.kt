package com.example.foundit.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foundit.data.model.Post
import com.example.foundit.databinding.FragmentMyPostsBinding
import com.example.foundit.ui.adapters.PostAdapter
import com.example.foundit.ui.viewmodel.PostViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class MyPostsFragment : Fragment() {

    private var _binding: FragmentMyPostsBinding? = null
    private val binding get() = _binding!!

    private val postViewModel: PostViewModel by viewModels()
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeMyPosts()
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(
            onItemClick = { post ->
                val action =
                    MyPostsFragmentDirections.actionMyPostsFragmentToItemDetailFragment(post.id)
                findNavController().navigate(action)
            },
            onEditClick = { post ->
                editPost(post)
            },
            onDeleteClick = { post ->
                showDeleteConfirmationDialog(post)
            },
            isMyPostsAdapter = true
        )

        binding.rvMyPosts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postAdapter
        }
    }

    private fun observeMyPosts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                try {
                    postViewModel.myPosts.collect { posts ->
                        if (posts.isEmpty()) {
                            binding.rvMyPosts.visibility = View.GONE
                            binding.tvNoPosts.visibility = View.VISIBLE
                        } else {
                            binding.rvMyPosts.visibility = View.VISIBLE
                            binding.tvNoPosts.visibility = View.GONE
                            postAdapter.submitList(posts)
                        }
                    }
                } catch (e: Exception) {
                    if (e !is CancellationException) {
                        Toast.makeText(
                            context,
                            "Failed to load posts: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog(post: Post) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton("Delete") { dialog, _ ->
                postViewModel.deletePost(post.id)
                Toast.makeText(context, "Post deleted successfully.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun editPost(post: Post) {
        val action = MyPostsFragmentDirections.actionMyPostsFragmentToReportFragment(post.id)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}