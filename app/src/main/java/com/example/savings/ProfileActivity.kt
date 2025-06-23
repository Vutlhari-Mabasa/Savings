package com.example.savings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.savings.R.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_profile)

        val emailText = findViewById<TextView>(id.emailText)
        val statusText = findViewById<TextView>(id.statusText) // Optional: for showing if email is verified
        val logoutButton = findViewById<Button>(id.logoutButton)

        val user = Firebase.auth.currentUser

        if (user != null) {
            val name = user.displayName
            val email = user.email
            val emailVerified = user.isEmailVerified
            val uid = user.uid

            emailText.text = "Logged in as: $email"
            statusText.text = if (emailVerified) "Email Verified" else "Email Not Verified"
        } else {
            emailText.text = "No user logged in"
        }

        logoutButton.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
