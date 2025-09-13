package com.example.foundit.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.foundit.R
import com.example.foundit.data.repository.AuthRepository
import com.example.foundit.data.repository.UserRepository
import com.example.foundit.databinding.FragmentProfileBinding
import com.example.foundit.ui.activities.LoginActivity
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val userRepository: UserRepository = UserRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        setupProfileInfo()
        setupLogoutButton()
    }

    private fun setupProfileInfo() {
        lifecycleScope.launch {
            val userId = AuthRepository.getCurrentUserId()
            if (userId != null) {
                val user = userRepository.getUserById(userId)
                if (user != null) {
                    binding.tvProfileName.text = user.name
                    binding.tvProfileEmail.text = user.email
                    binding.tvProfilePhone.text = user.phone

                    // Also set the header views
                    binding.tvProfileNameHeader.text = user.name
                    binding.tvProfileEmailHeader.text = user.email
                } else {
                    Toast.makeText(requireContext(), "User data not found.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupLogoutButton() {
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Log Out")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Log Out") { _, _ ->
                AuthRepository.signOut()
                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
