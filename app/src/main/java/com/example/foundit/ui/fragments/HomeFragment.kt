package com.example.foundit.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foundit.R
import com.example.foundit.data.repository.AuthRepository
import com.example.foundit.data.repository.UserRepository
import com.example.foundit.ui.adapters.RecentPostAdapter
import com.example.foundit.databinding.FragmentHomeBinding
import com.example.foundit.ui.viewmodel.PostViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val postViewModel: PostViewModel by viewModels()
    private val userRepository: UserRepository = UserRepository
    private lateinit var adapter: RecentPostAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        updateHeader()
        setupRecyclerView()
        observePosts()

        binding.cardReportLost.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeToCreatePost()
            findNavController().navigate(action)
        }

        binding.cardReportFound.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeToCreatePost()
            findNavController().navigate(action)
        }

        binding.btnSeeAll.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeToDiscover()
            findNavController().navigate(action)
        }
    }

    private fun updateHeader() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        lifecycleScope.launch {
            val userId = AuthRepository.getCurrentUserId()
            val userName = if (userId != null) {
                val user = userRepository.getUserById(userId)
                user?.name ?: "User"
            } else {
                "User"
            }

            binding.tvUserName.text = userName
        }
    }

    private fun setupRecyclerView() {
        adapter = RecentPostAdapter { post ->
            val action = HomeFragmentDirections.actionHomeToItemDetail(post.id)
            findNavController().navigate(action)
        }
        binding.rvRecentItems.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = this@HomeFragment.adapter
        }
    }

    private fun observePosts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postViewModel.allPosts.collect { posts ->
                    val recentPosts = posts.take(4)
                    adapter.submitList(recentPosts)

                    if (recentPosts.isEmpty()) {
                        binding.rvRecentItems.visibility = View.GONE
                        binding.emptyStateView.visibility = View.VISIBLE
                    } else {
                        binding.rvRecentItems.visibility = View.VISIBLE
                        binding.emptyStateView.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
