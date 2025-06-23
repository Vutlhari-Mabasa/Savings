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

        binding.buttonLogin.setOnClickListener {
            auth.signInWithEmailAndPassword(
                binding.editTextEmailLogin.text.toString().trim(),
                binding.editTextPasswordLogin.text.toString().trim()
            )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login success
                        Log.d(TAG, "signInWithEmail:success")
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
                    }
                }
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}