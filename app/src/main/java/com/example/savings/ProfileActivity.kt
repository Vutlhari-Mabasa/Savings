package com.example.savings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.savings.R.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ProfileActivity : AppCompatActivity() {

    private lateinit var notificationSwitch: Switch

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_profile)

        val emailText = findViewById<TextView>(id.emailText)
        val statusText = findViewById<TextView>(id.statusText)
        val logoutButton = findViewById<Button>(id.logoutButton)
        notificationSwitch = findViewById(R.id.notificationSwitch)

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

        // Set up notification switch
        notificationSwitch.isChecked = NotificationManager.isReminderScheduled(this)
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                NotificationManager.scheduleDailyReminder(this, 20, 0) // 8:00 PM
                Toast.makeText(this, "Daily expense reminders enabled!", Toast.LENGTH_SHORT).show()
            } else {
                NotificationManager.cancelDailyReminder(this)
                Toast.makeText(this, "Daily expense reminders disabled!", Toast.LENGTH_SHORT).show()
            }
        }

        logoutButton.setOnClickListener {
            // Cancel notifications when logging out
            NotificationManager.cancelDailyReminder(this)
            Firebase.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
