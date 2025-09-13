package com.example.foundit.ui.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.foundit.R
import com.example.foundit.data.model.Post
import com.example.foundit.data.repository.PostRepository
import com.example.foundit.databinding.FragmentItemDetailBinding
import com.example.foundit.utils.ImageLoader
import com.example.foundit.utils.formatTimestamp
import kotlinx.coroutines.launch

class ItemDetailFragment : Fragment(R.layout.fragment_item_detail) {

    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ItemDetailFragmentArgs by navArgs()

    private val CALL_PHONE_PERMISSION_CODE = 100

    private var posterPhoneNumber: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentItemDetailBinding.bind(view)

        setupHeader()
        fetchAndBindPostData()
    }

    private fun setupHeader() {
        binding.ivBackArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun fetchAndBindPostData() {
        val postId = args.postId
        lifecycleScope.launch {
            try {
                val post = PostRepository.getPostById(postId)
                if (post != null) {
                    posterPhoneNumber = post.phone
                    bindViews(post)
                    setupClickListeners()
                } else {
                    Toast.makeText(requireContext(), "Post not found.", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading post: ${e.message}", Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun bindViews(post: Post) {
        if (post.imageUrl.isNullOrBlank()) {
            binding.ivItemImage.setImageResource(R.drawable.ic_placeholder)
        } else {
            ImageLoader.loadImage(binding.ivItemImage, post.imageUrl)
        }

        binding.tvItemTitle.text = post.title
        binding.chipStatus.text = if (post.found) "Found" else "Lost"
        binding.tvItemDate.text = formatTimestamp(post.timestamp)
        binding.tvItemLocation.text = post.location
        binding.tvItemDescription.text = post.description
        binding.ivPosterProfile.setImageResource(R.drawable.avatar)
        binding.tvPosterName.text = post.postedBy
        binding.tvPosterPhone.text = post.phone.ifEmpty { "N/A" }
        binding.tvPosterMail.text = post.email.ifEmpty { "N/A" }

        binding.btnCall.visibility = if (post.phone.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun setupClickListeners() {
        binding.btnCall.setOnClickListener {
            posterPhoneNumber?.let { phoneNumber ->
                if (phoneNumber.isNotEmpty() && phoneNumber != "N/A") {
                    makeCall(phoneNumber)
                } else {
                    Toast.makeText(requireContext(), "Phone number is not available.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun makeCall(phoneNumber: String) {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CALL_PHONE), CALL_PHONE_PERMISSION_CODE)
        } else {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$phoneNumber")
            try {
                startActivity(callIntent)
            } catch (e: SecurityException) {
                Toast.makeText(requireContext(), "Permission denied. Unable to make a call.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CALL_PHONE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                posterPhoneNumber?.let {
                    makeCall(it)
                }
            } else {
                Toast.makeText(requireContext(), "Permission is required to make a call.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
