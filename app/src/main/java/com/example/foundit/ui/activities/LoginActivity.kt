package com.example.foundit.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.foundit.data.repository.AuthRepository
import com.example.foundit.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

            lifecycleScope.launch {
                try {
                    AuthRepository.signIn(email, password)
                    Toast.makeText(this@LoginActivity, "Logged in successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    handleLoginFailure(e)
                } finally {
                    setLoadingState(false)
                }
            }
        }

        binding.signupRedirect.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
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