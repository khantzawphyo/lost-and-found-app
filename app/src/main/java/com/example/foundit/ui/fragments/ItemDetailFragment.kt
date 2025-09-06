package com.example.foundit.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.foundit.R
import com.example.foundit.data.local.AppDatabase
import com.example.foundit.data.local.entities.Post
import com.example.foundit.databinding.FragmentItemDetailBinding
import kotlinx.coroutines.launch

class ItemDetailFragment : Fragment(R.layout.fragment_item_detail) {

    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ItemDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentItemDetailBinding.bind(view)

        setupToolbar()
        fetchAndBindPostData()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    /**
     * Fetches the post data from the database and binds it to the UI views.
     * It also sets up click listeners for the phone and email fields.
     */
    private fun fetchAndBindPostData() {
        val postId = args.postId
        val db = AppDatabase.getDatabase(requireContext())

        lifecycleScope.launch {
            val post = db.postDao().getPostById(postId)
            post?.let {
                bindViews(it)
                setupClickListeners(it)
            }
        }
    }

    /**
     * Binds the fetched post data to the views in the layout.
     */
    private fun bindViews(post: Post) {
        // Handle image loading, with a placeholder if the URI is null or empty
        if (!post.imageUri.isNullOrEmpty()) {
            binding.ivItemImage.setImageURI(post.imageUri.toUri())
        } else {
            binding.ivItemImage.setImageResource(R.drawable.ic_placeholder)
        }

        binding.tvItemTitle.text = post.title
        binding.chipStatus.text = if (post.isFound) "Found" else "Lost"
        binding.tvItemDate.text = post.date
        binding.tvItemLocation.text = post.location
        binding.tvItemDescription.text = post.description
        binding.ivPosterProfile.setImageResource(R.drawable.avatar)
        binding.tvPosterName.text = "Posted by ${post.postedBy}"
        binding.tvPosterPhone.text = post.phone.ifEmpty { "N/A" }
        binding.tvPosterMail.text = post.email.ifEmpty { "N/A" }
    }

    /**
     * Sets up the click listeners for the phone and email TextViews,
     * launching the appropriate intents.
     */
    private fun setupClickListeners(post: Post) {
        // Phone number click listener
        val phoneNumber = post.phone
        if (phoneNumber.isNotEmpty()) {
            binding.tvPosterPhone.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                }
                startIntent(intent, "No app found to handle phone calls.")
            }
        }

        // Email address click listener
        val emailAddress = post.email
        if (emailAddress.isNotEmpty()) {
            binding.tvPosterMail.setOnClickListener {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
                    putExtra(Intent.EXTRA_SUBJECT, "Regarding your posted item: ${post.title}")
                }
                startIntent(intent, "No email app found.")
            }
        }
    }

    /**
     * A helper function to safely start an intent and show a toast if it fails.
     */
    private fun startIntent(intent: Intent, failureMessage: String) {
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), failureMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}