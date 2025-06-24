package com.example.savings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.savings.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewProfileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.CreateExpenseButton.setOnClickListener {
            val intent = Intent(this, ExpenseActivity::class.java)
            startActivity(intent)
        }

        binding.GetFinanceAdviceBtn.setOnClickListener {
            val intent = Intent(this, AdviceActivity::class.java)
            startActivity(intent)
        }

        binding.budgetButton.setOnClickListener {
            val intent = Intent(this, BudgetActivity::class.java)
            startActivity(intent)
        }
    }
}
