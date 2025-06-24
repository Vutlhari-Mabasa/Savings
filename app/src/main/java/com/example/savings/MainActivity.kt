package com.example.savings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.savings.databinding.ActivityMainBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    // View binding for accessing layout views
    private lateinit var binding: ActivityMainBinding
    // Firestore database instance
    private lateinit var db: FirebaseFirestore
    // Firebase authentication instance
    private lateinit var auth: FirebaseAuth
    // PieChart view for spending visualization
    private lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore and Auth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Reference the PieChart from the layout
        pieChart = binding.pieChart
        setupPieChart() // Style and configure the PieChart
        loadSpendingData() // Load and display spending data from Firestore

        // Set up navigation button listeners
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

    // Configure the PieChart's appearance and behavior
    private fun setupPieChart() {
        pieChart.description.isEnabled = false // Remove default description
        pieChart.isDrawHoleEnabled = true // Draw a hole in the center
        pieChart.setEntryLabelColor(android.graphics.Color.BLACK)
        pieChart.setUsePercentValues(true)
        pieChart.setDrawEntryLabels(true)
        pieChart.legend.isEnabled = true
    }

    // Load expenses from Firestore and display category spending in the PieChart
    private fun loadSpendingData() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("expenses")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                // Aggregate total spent per category
                val categoryTotals = mutableMapOf<String, Float>()
                for (doc in snapshot) {
                    val category = doc.getString("category") ?: continue
                    val amount = doc.getDouble("amount")?.toFloat() ?: 0f
                    categoryTotals[category] = categoryTotals.getOrDefault(category, 0f) + amount
                }
                // Convert totals to PieChart entries
                val entries = categoryTotals.map { PieEntry(it.value, it.key) }
                val dataSet = PieDataSet(entries, "Spending by Category")
                dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
                val data = PieData(dataSet)
                data.setValueTextSize(14f)
                data.setValueTextColor(android.graphics.Color.BLACK)
                pieChart.data = data
                pieChart.invalidate() // Refresh the chart
            }
    }
}
