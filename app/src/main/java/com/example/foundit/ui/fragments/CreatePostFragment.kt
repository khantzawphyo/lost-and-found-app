package com.example.foundit.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.foundit.R
import com.example.foundit.data.model.Post
import com.example.foundit.data.repository.AuthRepository
import com.example.foundit.databinding.FragmentCreatePostBinding
import com.example.foundit.ui.viewmodel.PostViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreatePostFragment : Fragment(R.layout.fragment_create_post) {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private val postViewModel: PostViewModel by viewModels()
    // Retrieve navigation arguments for isFound and postId
    private val args: CreatePostFragmentArgs by navArgs()

    private var postId: String? = null
    private var isFound: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreatePostBinding.bind(view)

        // Get the postId and isFound from navigation arguments
        // postId will be null for new posts, and isFound will be true or false
        postId = args.postId
        isFound = args.isFound

        setupHeader()
        setupDatePicker()
        setupSubmitButton()

        // Check if in edit mode and populate the form
        if (postId != null) {
            setupEditMode(postId!!)
        }
    }

    private fun setupHeader() {
        val headerText = if (postId != null) {
            "Edit Post"
        } else {
            // Use the isFound boolean to determine the header text
            if (isFound) "Report a Found Item" else "Report a Lost Item"
        }
        binding.tvCreatePostHeader.text = headerText
        binding.ivBackArrow.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    val selectedDate = Calendar.getInstance().apply {
                        set(y, m, d)
                    }
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    binding.etDate.setText(dateFormat.format(selectedDate.time))
                },
                year, month, day
            )
            datePicker.show()
        }
    }

    private fun setupEditMode(postId: String) {
        setLoadingState(true)
        binding.btnSubmitPost.text = "Save Changes"

        lifecycleScope.launch {
            try {
                val post = postViewModel.getPostById(postId)
                if (post != null) {
                    binding.etItemName.setText(post.title)
                    binding.etDescription.setText(post.description)
                    binding.etLocation.setText(post.location)
                    binding.etDate.setText(post.date)
                    binding.etContactName.setText(post.postedBy)
                    binding.etPhone.setText(post.phone)
                    binding.etEmail.setText(post.email)
                } else {
                    Toast.makeText(requireContext(), "Post not found.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to load post: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                setLoadingState(false)
            }
        }
    }

    private fun setupSubmitButton() {
        binding.btnSubmitPost.setOnClickListener {
            val name = binding.etItemName.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()
            val date = binding.etDate.text.toString().trim()
            val contact = binding.etContactName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()

            if (name.isEmpty() || description.isEmpty() || location.isEmpty() || contact.isEmpty() || phone.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            } else {
                setLoadingState(true)
                lifecycleScope.launch {
                    try {
                        val userId = AuthRepository.getCurrentUserId() ?: run {
                            Toast.makeText(requireContext(), "User not authenticated. Please log in.", Toast.LENGTH_LONG).show()
                            setLoadingState(false)
                            return@launch
                        }

                        val post = Post(
                            id = postId ?: "", // Use existing ID for edits, empty for new
                            userId = userId,
                            title = name,
                            description = description,
                            location = location,
                            date = date.ifEmpty { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().time) },
                            imageUri = null,
                            postedBy = contact,
                            isFound = isFound,
                            phone = phone,
                            email = email.ifEmpty { "N/A" }
                        )

                        if (postId != null) {
                            postViewModel.updatePost(post)
                            Toast.makeText(requireContext(), "Post updated!", Toast.LENGTH_SHORT).show()
                        } else {
                            postViewModel.savePost(post)
                            Toast.makeText(requireContext(), "Report submitted!", Toast.LENGTH_SHORT).show()
                        }

                        findNavController().navigateUp()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Failed to submit report: ${e.message}", Toast.LENGTH_LONG).show()
                    } finally {
                        setLoadingState(false)
                    }
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.btnSubmitPost.isEnabled = !isLoading
        binding.tilItemName.isEnabled = !isLoading
        binding.tilDescription.isEnabled = !isLoading
        binding.tilLocation.isEnabled = !isLoading
        binding.tilDate.isEnabled = !isLoading
        binding.tilContactName.isEnabled = !isLoading
        binding.tilPhone.isEnabled = !isLoading
        binding.tilEmail.isEnabled = !isLoading
        binding.pbSubmit.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSubmitPost.text = if (isLoading) "" else if (postId != null) "Save Changes" else "Create Post"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
