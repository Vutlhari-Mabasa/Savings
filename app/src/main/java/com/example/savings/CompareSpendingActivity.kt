package com.example.savings

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CompareSpendingActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private lateinit var summaryText: TextView
    private lateinit var backButton: ImageButton
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compare_spending)

        // Initialize views
        barChart = findViewById(R.id.barChart)
        summaryText = findViewById(R.id.summaryText)
        backButton = findViewById(R.id.backButton)

        // Initialize Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Set up back button
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Set up chart
        setupBarChart()

        // Load monthly spending data
        loadMonthlySpending()
    }

    private fun setupBarChart() {
        barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            
            // X axis
            xAxis.apply {
                setDrawGridLines(false)
                setDrawAxisLine(true)
                textColor = android.graphics.Color.WHITE
                textSize = 12f
            }
            
            // Y axis
            axisLeft.apply {
                setDrawGridLines(true)
                setDrawAxisLine(true)
                textColor = android.graphics.Color.WHITE
                textSize = 12f
            }
            
            axisRight.isEnabled = false
            
            // Legend
            legend.apply {
                textColor = android.graphics.Color.WHITE
                textSize = 12f
            }
            
            // Animation
            animateY(1000)
        }
    }

    private fun loadMonthlySpending() {
        val uid = auth.currentUser?.uid ?: return
        
        // Get current date and calculate 3 months ago
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        
        val monthlyData = mutableListOf<Pair<String, Double>>()
        
        // Calculate the start date for 3 months ago
        val startDate = Calendar.getInstance().apply {
            set(currentYear, currentMonth - 2, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        // Query all expenses for the last 3 months in one go
        db.collection("expenses")
            .whereEqualTo("userId", uid)
            .whereGreaterThanOrEqualTo("date", startDate)
            .get()
            .addOnSuccessListener { snapshot ->
                // Group expenses by month
                val monthlyTotals = mutableMapOf<String, Double>()
                
                for (doc in snapshot.documents) {
                    val date = doc.getLong("date") ?: continue
                    val amount = doc.getDouble("amount") ?: 0.0
                    
                    // Convert timestamp to month name
                    val monthName = SimpleDateFormat("MMM yyyy", Locale.getDefault())
                        .format(Date(date))
                    
                    monthlyTotals[monthName] = monthlyTotals.getOrDefault(monthName, 0.0) + amount
                }
                
                // Sort by date and take the last 3 months
                val sortedMonths = monthlyTotals.entries.sortedBy { 
                    SimpleDateFormat("MMM yyyy", Locale.getDefault()).parse(it.key)?.time ?: 0L 
                }.takeLast(3)
                
                // Convert to list format expected by updateChart
                monthlyData.addAll(sortedMonths.map { Pair(it.key, it.value) })
                
                if (monthlyData.isNotEmpty()) {
                    updateChart(monthlyData)
                } else {
                    summaryText.text = "No spending data found for the last 3 months"
                }
            }
            .addOnFailureListener { e ->
                // Handle error
                summaryText.text = "Error loading data: ${e.message}"
            }
    }

    private fun updateChart(monthlyData: List<Pair<String, Double>>) {
        val entries = monthlyData.mapIndexed { index, (_, amount) ->
            BarEntry(index.toFloat(), amount.toFloat())
        }
        
        val dataSet = BarDataSet(entries, "Monthly Spending (R)")
        dataSet.apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextColor = android.graphics.Color.WHITE
            valueTextSize = 12f
        }
        
        val barData = BarData(dataSet)
        barChart.data = barData
        
        // Set X axis labels
        val labels = monthlyData.map { it.first }
        barChart.xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value.toInt() < labels.size) labels[value.toInt()] else ""
            }
        }
        
        barChart.invalidate()
        
        // Update summary text with comparison
        updateSummaryText(monthlyData)
    }

    private fun updateSummaryText(monthlyData: List<Pair<String, Double>>) {
        if (monthlyData.size < 2) {
            summaryText.text = "Monthly Spending Comparison\nNot enough data for comparison"
            return
        }
        
        val currentMonth = monthlyData.last().second
        val previousMonth = monthlyData[monthlyData.size - 2].second
        
        val difference = currentMonth - previousMonth
        val percentageChange = if (previousMonth > 0) (difference / previousMonth) * 100 else 0.0
        
        val summary = buildString {
            append("Monthly Spending Comparison\n")
            append("Current: R${"%.2f".format(currentMonth)}\n")
            append("Previous: R${"%.2f".format(previousMonth)}\n")
            when {
                difference > 0 -> append("📈 Increased by R${"%.2f".format(difference)} (${"%.1f".format(percentageChange)}%)")
                difference < 0 -> append("📉 Decreased by R${"%.2f".format(-difference)} (${"%.1f".format(-percentageChange)}%)")
                else -> append("➡️ No change")
            }
        }
        
        summaryText.text = summary
    }
} 