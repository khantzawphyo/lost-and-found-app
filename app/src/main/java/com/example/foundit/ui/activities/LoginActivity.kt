package com.example.foundit.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.foundit.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            binding.textInputLayoutEmail.error = null
            binding.textInputLayoutPassword.error = null

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.textInputLayoutEmail.error = "Email is required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.textInputLayoutPassword.error = "Password is required"
                return@setOnClickListener
            }

            // Show loading state and disable button
            setLoadingState(true)

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    // Hide loading state
                    setLoadingState(false)

                    if (task.isSuccessful) {
                        Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        // Handle failed login
                        handleLoginFailure(task.exception)
                    }
                }
        }

        binding.tvSignupRedirect.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.btnLogin.text = ""
            binding.progressBar.visibility = View.VISIBLE
            binding.btnLogin.isEnabled = false
        } else {
            binding.btnLogin.text = "Login"
            binding.progressBar.visibility = View.GONE
            binding.btnLogin.isEnabled = true
        }
    }

    private fun handleLoginFailure(exception: Exception?) {
        when (exception) {
            is FirebaseAuthInvalidUserException -> {
                binding.textInputLayoutEmail.error = "User not found or has been disabled."
            }
            is FirebaseAuthInvalidCredentialsException -> {
                binding.textInputLayoutPassword.error = "Incorrect password. Please try again."
                binding.etPassword.text?.clear()
            }
            is FirebaseNetworkException -> {
                Toast.makeText(this, "A network error occurred. Please check your internet connection.", Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this, "Login failed. Please try again later.", Toast.LENGTH_LONG).show()
            }
        }
    }
}