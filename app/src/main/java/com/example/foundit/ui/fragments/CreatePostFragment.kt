package com.example.foundit.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.foundit.R
import com.example.foundit.data.model.Post
import com.example.foundit.data.repository.AuthRepository
import com.example.foundit.data.repository.UserRepository
import com.example.foundit.databinding.FragmentCreatePostBinding
import com.example.foundit.ui.viewmodel.PostViewModel
import com.example.foundit.utils.ImageLoader
import com.example.foundit.utils.ImageUploader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreatePostFragment : Fragment(R.layout.fragment_create_post) {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private val postViewModel: PostViewModel by viewModels()
    private val userRepository: UserRepository = UserRepository
    private val args: CreatePostFragmentArgs by navArgs()

    private var postId: String? = null
    private var found: Boolean = false
    private var imageUri: Uri? = null
    private var existingImageUrl: String? = null
    private var existingPostedBy: String = ""


    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            binding.ivItemUpload.setImageURI(imageUri)
            binding.ivItemUpload.visibility = View.VISIBLE
            binding.ivPlaceholder.visibility = View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreatePostBinding.bind(view)

        postId = args.postId

        setupHeader()
        setupReportTypeToggle()
        setupImagePickerButton()
        setupSubmitButton()

        if (postId != null) {
            setupEditMode(postId!!)
        } else {
            prefillUserInfo()
        }
    }

    private fun setupHeader() {
        val headerText = if (postId != null) {
            "Edit Post"
        } else {
            "Create Post"
        }
        binding.tvCreatePostHeader.text = headerText
        binding.ivBackArrow.setOnClickListener {
            findNavController().navigateUp()
        }
    }


    private fun setupReportTypeToggle() {
        binding.btnLost.isChecked = true
        found = false
        binding.toggleButtonGroupReportType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                found = checkedId == binding.btnFound.id
            }
        }
    }


    private fun prefillUserInfo() {
        lifecycleScope.launch {
            val userId = AuthRepository.getCurrentUserId()
            if (userId != null) {
                val user = userRepository.getUserById(userId)
                if (user != null) {
                    binding.etPhone.setText(user.phone)
                    binding.etEmail.setText(user.email)
                }
            }
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
                    binding.etPhone.setText(post.phone)
                    binding.etEmail.setText(post.email)

                    found = post.found
                    if (found) {
                        binding.btnFound.isChecked = true
                    } else {
                        binding.btnLost.isChecked = true
                    }

                    existingPostedBy = post.postedBy


                    if(!post.imageUrl.isNullOrEmpty()) {
                        existingImageUrl = post.imageUrl
                        ImageLoader.loadImage(binding.ivItemUpload, existingImageUrl)
                        binding.ivItemUpload.visibility = View.VISIBLE
                        binding.ivPlaceholder.visibility = View.INVISIBLE
                    }

                } else {
                    Toast.makeText(requireContext(), "Post not found.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("CreatePostFragment", "Failed to load post", e)
                Toast.makeText(requireContext(), "Failed to load post: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                setLoadingState(false)
            }
        }
    }

    private fun setupImagePickerButton() {
        binding.btnUploadImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun setupSubmitButton() {
        binding.btnSubmitPost.setOnClickListener {
            val name = binding.etItemName.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()

            if (name.isEmpty() || description.isEmpty() || location.isEmpty() || phone.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            } else {
                setLoadingState(true)
                lifecycleScope.launch {
                    try {
                        val postTimestamp = System.currentTimeMillis()
                        var imageUrl: String? = null

                        if (imageUri != null) {
                            withContext(Dispatchers.IO) {
                                imageUrl = ImageUploader.uploadImage(requireContext(), imageUri!!)
                            }
                            if (imageUrl == null) {
                                Toast.makeText(requireContext(), "Image upload failed. Please try again.", Toast.LENGTH_LONG).show()
                                return@launch
                            }
                        } else {
                            imageUrl = existingImageUrl
                        }

                        if (postId != null) {
                            // edit post
                            val updatedPost = Post(
                                id = postId!!,
                                userId = AuthRepository.getCurrentUserId()!!,
                                title = name,
                                description = description,
                                location = location,
                                timestamp = postTimestamp,
                                imageUrl = imageUrl,
                                found = found,
                                phone = phone,
                                email = email.ifEmpty { "N/A" },
                                postedBy = existingPostedBy
                            )
                            postViewModel.updatePost(updatedPost)
                            Toast.makeText(requireContext(), "Post updated!", Toast.LENGTH_SHORT).show()
                        } else {
                            // new post
                            postViewModel.createAndSavePost(
                                title = name,
                                description = description,
                                location = location,
                                timestamp = postTimestamp,
                                imageUrl = imageUrl,
                                found = found,
                                phone = phone,
                                email = email.ifEmpty { "N/A" }
                            )
                            Toast.makeText(requireContext(), "Report submitted!", Toast.LENGTH_SHORT).show()
                        }
                        findNavController().navigateUp()
                    } catch (e: Exception) {
                        Log.e("CreatePostFragment", "Failed to submit report", e)
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
        binding.toggleButtonGroupReportType.isEnabled = !isLoading
        binding.btnUploadImage.isEnabled = !isLoading
        binding.tilItemName.isEnabled = !isLoading
        binding.tilDescription.isEnabled = !isLoading
        binding.tilLocation.isEnabled = !isLoading
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
