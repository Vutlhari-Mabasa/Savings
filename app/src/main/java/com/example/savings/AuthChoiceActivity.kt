package com.example.savings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.savings.databinding.ActivityAuthChoiceBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

// Activity for users to choose between login and register
class AuthChoiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthChoiceBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityAuthChoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Configure Firebase Auth to persist user sessions for 2 months
        // Firebase Auth automatically handles persistence, but we can configure it
        // The default persistence is LOCAL, which persists until explicitly signed out

        // Check if user is already logged in
        checkUserLoginStatus()

        // Set up button listeners
        setupClickListeners()
    }

    // Check if user is already logged in and redirect to MainActivity
    private fun checkUserLoginStatus() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is logged in, go to main activity
            // Firebase Auth automatically handles session persistence
            // Users will stay logged in until they explicitly sign out or the session expires
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Set up navigation buttons
    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
} 