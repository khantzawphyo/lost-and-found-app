package com.example.foundit.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.foundit.databinding.ActivitySignupBinding
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.auth

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnSignup.setOnClickListener {
            // Clear previous errors
            binding.textInputLayoutName.error = null
            binding.textInputLayoutEmail.error = null
            binding.textInputLayoutPassword.error = null
            binding.textInputLayoutConfirmPassword.error = null

            val fullName = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            // Input validation
            if (fullName.isEmpty()) {
                binding.textInputLayoutName.error = "Name is required"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                binding.textInputLayoutEmail.error = "Email is required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.textInputLayoutPassword.error = "Password is required"
                return@setOnClickListener
            }
            if (confirmPassword.isEmpty()) {
                binding.textInputLayoutConfirmPassword.error = "Confirm Password is required"
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                binding.textInputLayoutPassword.error = "Passwords do not match"
                binding.textInputLayoutConfirmPassword.error = "Passwords do not match"
                binding.etPassword.text?.clear()
                binding.etConfirmPassword.text?.clear()
                return@setOnClickListener
            }

            // Show loading state and disable button
            setLoadingState(true)

            // Create Firebase user
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    // Hide loading state
                    setLoadingState(false)

                    if (task.isSuccessful) {
                        // User created successfully, navigate to the main activity
                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        // Signup failed, handle the specific exception
                        handleSignupFailure(task.exception)
                    }
                }
        }

        binding.tvLoginRedirect.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.btnSignup.text = ""
            binding.progressBar.visibility = View.VISIBLE
            binding.btnSignup.isEnabled = false
        } else {
            binding.btnSignup.text = "Sign Up"
            binding.progressBar.visibility = View.GONE
            binding.btnSignup.isEnabled = true
        }
    }

    private fun handleSignupFailure(exception: Exception?) {
        when (exception) {
            is FirebaseAuthWeakPasswordException -> {
                binding.textInputLayoutPassword.error = "Password is too weak. It must be at least 6 characters."
                binding.etPassword.text?.clear()
                binding.etConfirmPassword.text?.clear()
            }
            is FirebaseAuthInvalidCredentialsException -> {
                binding.textInputLayoutEmail.error = "The email address is not valid."
            }
            is FirebaseAuthUserCollisionException -> {
                binding.textInputLayoutEmail.error = "The email address is already in use by another account."
            }
            is FirebaseNetworkException -> {
                Toast.makeText(this, "A network error occurred. Please check your internet connection.", Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this, "Signup failed. Please try again later.", Toast.LENGTH_LONG).show()
            }
        }
    }
}