package com.example.foundit.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foundit.data.model.Post
import com.example.foundit.databinding.FragmentDiscoverBinding
import com.example.foundit.ui.adapters.PostAdapter
import com.example.foundit.ui.viewmodel.PostViewModel
import kotlinx.coroutines.launch

class DiscoverFragment : Fragment() {

    private var _binding: FragmentDiscoverBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PostAdapter

    private val postViewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observePosts()

        binding.ivBackArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnCreatePost.setOnClickListener {
            val action = DiscoverFragmentDirections.actionDiscoverToCreatePost()
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() {
        adapter = PostAdapter(
            onItemClick = { post ->
            val action = DiscoverFragmentDirections.actionDiscoverToItemDetail(post.id)
            findNavController().navigate(action)
        }, onEditClick = { }, onDeleteClick = { }, isMyPostsAdapter = false
        )
        binding.rvAllPosts.layoutManager = LinearLayoutManager(context)
        binding.rvAllPosts.adapter = adapter
    }

    private fun observePosts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postViewModel.allPosts.collect { posts ->
                    adapter.submitList(posts)
                    updateUiForResults(posts)
                }
            }
        }
    }

    private fun updateUiForResults(posts: List<Post>) {
        if (posts.isEmpty()) {
            binding.emptyStateView.visibility = View.VISIBLE
            binding.rvAllPosts.visibility = View.GONE
        } else {
            binding.emptyStateView.visibility = View.GONE
            binding.rvAllPosts.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
