package com.example.savings

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.savings.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Set up login button
        binding.buttonLogin.setOnClickListener {
            performLogin()
        }

        // Set up back button to return to choice page
        binding.buttonBackToChoice.setOnClickListener {
            val intent = Intent(this, AuthChoiceActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Handle login process
    private fun performLogin() {
        val email = binding.editTextEmailLogin.text.toString().trim()
        val password = binding.editTextPasswordLogin.text.toString().trim()

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading state
        binding.buttonLogin.isEnabled = false
        binding.buttonLogin.text = "Logging in..."

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login success
                    Log.d(TAG, "signInWithEmail:success")
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Login failed
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Login failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG,
                    ).show()
                    // Reset button state
                    binding.buttonLogin.isEnabled = true
                    binding.buttonLogin.text = "Login"
                }
            }
    }

    // Handle back button press
    override fun onBackPressed() {
        val intent = Intent(this, AuthChoiceActivity::class.java)
        startActivity(intent)
        finish()
    }
}