package com.example.foundit.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foundit.R
import com.example.foundit.ui.adapters.PostAdapter
import com.example.foundit.data.model.Post
import com.example.foundit.databinding.FragmentDiscoverBinding
import com.example.foundit.ui.viewmodel.PostViewModel

class DiscoverFragment : Fragment(R.layout.fragment_discover) {

    private var _binding: FragmentDiscoverBinding? = null
    private val binding get() = _binding!!
    private val postViewModel: PostViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDiscoverBinding.bind(view)

        val adapter = PostAdapter { post ->
            val action = DiscoverFragmentDirections.actionDiscoverFragmentToItemDetailFragment(post.id)
            findNavController().navigate(action)
        }

        binding.listItemsView.layoutManager = LinearLayoutManager(requireContext())
        binding.listItemsView.adapter = adapter

        binding.btnReport.setOnClickListener {
            val action = DiscoverFragmentDirections.actionDiscoverFragmentToReportFragment()
            findNavController().navigate(action)
        }

        postViewModel.allPosts.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
            updateUiForResults(posts)
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun updateUiForResults(posts: List<Post>) {
        if (posts.isEmpty()) {
            binding.listItemsView.visibility = View.GONE
            binding.emptyStateView.visibility = View.VISIBLE
        } else {
            binding.listItemsView.visibility = View.VISIBLE
            binding.emptyStateView.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}