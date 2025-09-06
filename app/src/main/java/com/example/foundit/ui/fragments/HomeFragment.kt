package com.example.foundit.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foundit.R
import com.example.foundit.databinding.FragmentHomeBinding
import com.example.foundit.ui.adapters.RecentPostAdapter
import com.example.foundit.ui.viewmodel.PostViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val postViewModel: PostViewModel by viewModels()
    private lateinit var adapter: RecentPostAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        updateHeader()
        setupRecyclerView()
        observePosts()

        binding.cardReportCta.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToReportFragment()
            findNavController().navigate(action)
        }

        binding.tvSeeAll.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDiscoverFragment()
            findNavController().navigate(action)
        }
    }

    private fun updateHeader() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val greeting = when (hour) {
            in 0..11 -> "Good Morning!"
            in 12..17 -> "Good Afternoon!"
            else -> "Good Evening!"
        }
        binding.tvGreeting.text = greeting

        val dateFormat = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault())
        binding.tvDate.text = "It's ${dateFormat.format(calendar.time)}"
    }

    private fun setupRecyclerView() {
        adapter = RecentPostAdapter(emptyList()) { post ->
            val action = HomeFragmentDirections.actionHomeFragmentToItemDetailFragment(post.id)
            findNavController().navigate(action)
        }
        binding.rvRecentItems.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = this@HomeFragment.adapter
        }
    }

    private fun observePosts() {
        postViewModel.allPosts.observe(viewLifecycleOwner) { posts ->
            val recentPosts = posts.take(4) // Only latest 4 posts
            adapter.updatePosts(recentPosts)

            if(recentPosts.isEmpty()) {
                binding.rvRecentItems.visibility = View.GONE
                binding.emptyStateView.visibility = View.VISIBLE
            }
            else {
                binding.rvRecentItems.visibility = View.VISIBLE
                binding.emptyStateView.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
