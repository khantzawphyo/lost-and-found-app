package com.example.foundit.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.foundit.R
import com.example.foundit.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        auth = FirebaseAuth.getInstance()
        setupProfileInfo()
        setupLogoutButton()
    }

    private fun setupProfileInfo() {
        val user = auth.currentUser
        if (user != null) {
            // Display info from Firebase User
            binding.tvProfileNameHeader.text = user.displayName ?: "User"
            binding.tvProfileName.text = user.displayName ?: "User"
            binding.tvProfileEmailHeader.text = user.email
            binding.tvProfileEmail.text = user.email
        } else {
            Toast.makeText(requireContext(), "No user logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupLogoutButton() {
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
            // Navigate back to LoginActivity
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
