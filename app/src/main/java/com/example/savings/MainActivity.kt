package com.example.savings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

    // Permission launcher for notifications
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            setupDailyReminder()
            Toast.makeText(this, "Daily expense reminders enabled!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission denied. Reminders won't work.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore and Auth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        
        // Firebase Auth automatically handles persistent login:
        // - User stays logged in across app restarts
        // - Session persists until user explicitly signs out
        // - No additional code needed for persistence

        // Reference the PieChart from the layout
        pieChart = binding.pieChart
        setupPieChart() // Style and configure the PieChart
        loadSpendingData() // Load and display spending data from Firestore

        // Set up notification channel
        ExpenseReminderService.createNotificationChannel(this)

        // Set up navigation button listeners
        binding.profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.CreateExpenseButton.setOnClickListener {
            val intent = Intent(this, ExpenseActivity::class.java)
            startActivity(intent)
        }

        binding.budgetButton.setOnClickListener {
            val intent = Intent(this, BudgetActivity::class.java)
            startActivity(intent)
        }

        binding.compareSpendingButton.setOnClickListener {
            val intent = Intent(this, CompareSpendingActivity::class.java)
            startActivity(intent)
        }

        // Set up navigation drawer
        setupNavigationDrawer()

        // Set up daily reminder when user first opens the app
        setupDailyReminder()
    }

    private fun setupDailyReminder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted, schedule reminder
                    scheduleReminder()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show rationale and request permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Request permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // For older Android versions, schedule directly
            scheduleReminder()
        }
    }

    private fun scheduleReminder() {
        if (!NotificationManager.isReminderScheduled(this)) {
            NotificationManager.scheduleDailyReminder(this, 20, 0) // 8:00 PM
        }
    }

    // Configure the PieChart's appearance and behavior
    private fun setupPieChart() {
        pieChart.apply {
            // Basic settings
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(android.graphics.Color.TRANSPARENT)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            centerText = "Total"
            setCenterTextSize(16f)
            setCenterTextColor(android.graphics.Color.WHITE)
            
            // Entry labels
            setEntryLabelColor(android.graphics.Color.WHITE)
            setEntryLabelTextSize(12f)
            setDrawEntryLabels(true)
            
            // Disable legend since we'll create custom one
            legend.isEnabled = false
            
            // Animations
            animateY(1400, com.github.mikephil.charting.animation.Easing.EaseInOutQuad)
            animateX(1400, com.github.mikephil.charting.animation.Easing.EaseInOutQuad)
            
            // Rotation
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            
            // Extra offsets
            setExtraOffsets(5f, 10f, 5f, 5f)
        }
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
                
                if (categoryTotals.isNotEmpty()) {
                    // Convert totals to PieChart entries
                    val entries = categoryTotals.map { PieEntry(it.value, it.key) }
                    val dataSet = PieDataSet(entries, "Spending by Category")
                    
                    // Custom colors for better visual appeal
                    val colors = listOf(
                        Color.rgb(64, 89, 128),   // Blue
                        Color.rgb(149, 165, 124), // Green
                        Color.rgb(217, 184, 162), // Beige
                        Color.rgb(191, 134, 134), // Pink
                        Color.rgb(179, 48, 80),   // Red
                        Color.rgb(193, 37, 82),   // Dark Red
                        Color.rgb(255, 102, 0),   // Orange
                        Color.rgb(245, 199, 0),   // Yellow
                        Color.rgb(106, 150, 31),  // Light Green
                        Color.rgb(179, 100, 53)   // Brown
                    )
                    dataSet.colors = colors
                    
                    // Data set styling
                    dataSet.apply {
                        valueTextSize = 14f
                        valueTextColor = Color.WHITE
                        valueLinePart1Length = 0.4f
                        valueLinePart2Length = 0.4f
                        valueLineColor = Color.WHITE
                        yValuePosition = com.github.mikephil.charting.data.PieDataSet.ValuePosition.OUTSIDE_SLICE
                        valueLineWidth = 1f
                        valueLinePart1OffsetPercentage = 80f
                        valueLinePart2Length = 0.4f
                        valueLineColor = Color.WHITE
                        sliceSpace = 3f
                        selectionShift = 5f
                        // Remove value labels around the chart
                        setDrawValues(false)
                    }
                    
                    val data = PieData(dataSet)
                    data.setValueFormatter(com.github.mikephil.charting.formatter.PercentFormatter(pieChart))
                    pieChart.data = data
                    pieChart.invalidate() // Refresh the chart
                    
                    // Create custom legend
                    createCustomLegend(categoryTotals, colors)
                } else {
                    // Show empty state
                    pieChart.clear()
                    pieChart.invalidate()
                    clearCustomLegend()
                }
            }
    }

    private fun createCustomLegend(categoryTotals: Map<String, Float>, colors: List<Int>) {
        val legendContainer = binding.legendContainer
        legendContainer.removeAllViews()
        
        // Calculate total for percentage calculation
        val total = categoryTotals.values.sum()
        
        // Add title
        val titleView = TextView(this).apply {
            text = "Spending Categories"
            setTextColor(Color.WHITE)
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
        }
        legendContainer.addView(titleView)
        
        // Add legend items
        categoryTotals.entries.forEachIndexed { index, (category, amount) ->
            val legendItem = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 8
                }
            }
            
            // Color indicator
            val colorView = TextView(this).apply {
                width = 24
                height = 24
                setBackgroundColor(colors[index % colors.size])
                layoutParams = LinearLayout.LayoutParams(24, 24).apply {
                    marginEnd = 12
                }
            }
            
            // Category name and percentage
            val percentage = if (total > 0) (amount / total * 100) else 0f
            val textView = TextView(this).apply {
                text = "$category: ${String.format("%.1f", percentage)}%"
                setTextColor(Color.WHITE)
                textSize = 14f
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            
            legendItem.addView(colorView)
            legendItem.addView(textView)
            legendContainer.addView(legendItem)
        }
    }

    private fun clearCustomLegend() {
        val legendContainer = binding.legendContainer
        legendContainer.removeAllViews()
        
        val emptyView = TextView(this).apply {
            text = "No expenses to display"
            setTextColor(Color.WHITE)
            textSize = 14f
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        legendContainer.addView(emptyView)
    }

    private fun setupNavigationDrawer() {
        val navigationView = binding.navigationView
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> {
                    // Already on dashboard, just close drawer
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_expenses -> {
                    val intent = Intent(this, ExpenseActivity::class.java)
                    startActivity(intent)
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_budget -> {
                    val intent = Intent(this, BudgetActivity::class.java)
                    startActivity(intent)
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    binding.drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }
    }
}
