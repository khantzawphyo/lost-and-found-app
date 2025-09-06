package com.example.foundit.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.foundit.R
import com.example.foundit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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