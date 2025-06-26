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

        // Firebase Auth automatically handles persistent login with these behaviors:
        // 1. LOCAL persistence (default): User stays logged in until explicitly signed out
        // 2. Session persists across app restarts, device reboots, and app updates
        // 3. Session typically lasts for 1 year unless user signs out
        // 4. No additional configuration needed - Firebase handles everything automatically

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
            // This happens automatically when the app starts if user was previously logged in
            // Firebase Auth automatically restores the user session from local storage
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Close this activity so user can't go back to auth choice
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