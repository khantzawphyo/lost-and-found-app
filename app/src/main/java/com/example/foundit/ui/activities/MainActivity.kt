package com.example.foundit.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.foundit.R
import com.example.foundit.data.repository.AuthRepository // Import AuthRepository
import com.example.foundit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        // Check if the user is logged in before setting up any UI
        if (!AuthRepository.isUserLoggedIn()) {
            // User is NOT logged in, redirect to the Welcome screen
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish() // Prevent the user from returning to this screen via the back button
            return // Stop further execution of onCreate
        }


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setOnItemSelectedListener { item -> when(item.itemId) {
            R.id.menu_home -> {
                if (navController.currentDestination?.id != R.id.homeFragment) {
                    navController.navigate(R.id.homeFragment)
                }
                true
            }
            R.id.menu_discover -> {
                if (navController.currentDestination?.id != R.id.discoverFragment) {
                    navController.navigate(R.id.discoverFragment)
                }
                true
            }
            R.id.menu_my_posts -> {
                if(navController.currentDestination?.id != R.id.myPostsFragment) {
                    navController.navigate(R.id.myPostsFragment)
                }
                true
            }
            R.id.menu_profile -> {
                if (navController.currentDestination?.id != R.id.profileFragment) {
                    navController.navigate(R.id.profileFragment)
                }
                true
            }

            else -> false
        } }
    }
}