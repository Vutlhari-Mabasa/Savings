package com.example.savings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth

class ExpenseReminderReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        // Check if user is logged in before showing notification
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Show the expense reminder notification
            ExpenseReminderService.showExpenseReminder(context)
        }
    }
} 