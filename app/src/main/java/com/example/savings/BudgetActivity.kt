package com.example.savings

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.savings.data.Budget
import com.example.savings.data.BudgetAdapter
import com.example.savings.data.Expense
import com.example.savings.databinding.ActivityBudgetBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

// Activity for managing user budget goals
class BudgetActivity : AppCompatActivity() {

    // View binding for accessing layout views
    private lateinit var binding: ActivityBudgetBinding
    // Adapter for displaying budgets in a RecyclerView
    private lateinit var budgetAdapter: BudgetAdapter
    // Local list of budgets (synced with Firestore)
    private val budgets = mutableListOf<Budget>()
    // Currently editing budget (if any)
    private var editingBudget: Budget? = null
    // Firestore database instance
    private lateinit var db: FirebaseFirestore
    // Firebase authentication instance
    private lateinit var auth: FirebaseAuth
    // List of expenses for calculating spent amounts
    private var expenses: List<Expense> = emptyList()

    // Categories available for budgets and expenses
    private val categories = listOf(
        "Food",
        "Transport",
        "Health",
        "Shopping",
        "Housing Fund",
        "Emergency Fund",
        "Bills",
        "Entertainment",
        "Utilities",
        "Other"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using view binding
        binding = ActivityBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore and Auth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setupRecyclerView() // Set up the budget list
        setupCategorySpinner() // Set up the category dropdown
        setupClickListeners() // Set up add/update button
        observeBudgetsAndExpenses() // Listen to Firestore for budgets and expenses
    }

    // Set up the RecyclerView and its adapter
    private fun setupRecyclerView() {
        budgetAdapter = BudgetAdapter(
            budgets = budgets,
            onEditClick = { budget -> editBudget(budget) },
            onDeleteClick = { budget -> showDeleteConfirmation(budget) }
        )

        binding.budgetsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@BudgetActivity)
            adapter = budgetAdapter
        }
    }

    // Set up the category dropdown with available categories
    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.categorySpinner.setAdapter(adapter)
    }

    // Set up the add/update button click logic
    private fun setupClickListeners() {
        binding.addBudgetButton.setOnClickListener {
            if (editingBudget != null) {
                updateBudget()
            } else {
                addBudget()
            }
        }
    }

    // Listen to Firestore for changes in budgets and expenses
    private fun observeBudgetsAndExpenses() {
        val uid = auth.currentUser?.uid ?: return
        // Listen to budgets for the current user
        db.collection("budgets")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { budgetSnapshot, e ->
                if (e != null || budgetSnapshot == null) return@addSnapshotListener
                budgets.clear()
                for (doc in budgetSnapshot) {
                    val budget = doc.toObject(Budget::class.java).copy(id = doc.id)
                    budgets.add(budget)
                }
                // After loading budgets, load expenses
                loadExpensesAndUpdateBudgets()
            }
    }

    // Listen to Firestore for changes in expenses and update budgets
    private fun loadExpensesAndUpdateBudgets() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("expenses")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { expenseSnapshot, e ->
                if (e != null || expenseSnapshot == null) return@addSnapshotListener
                expenses = expenseSnapshot.map { it.toObject(Expense::class.java).copy(id = it.id) }
                updateBudgetsSpentAmount()
            }
    }

    // For each budget, sum expenses for that category and update spentAmount
    private fun updateBudgetsSpentAmount() {
        val updatedBudgets = budgets.map { budget ->
            val spent = expenses.filter { it.category == budget.category }.sumOf { it.amount }
            budget.copy(spentAmount = spent)
        }
        budgetAdapter.updateBudgets(updatedBudgets)
    }

    // Add a new budget to Firestore
    private fun addBudget() {
        val category = binding.categorySpinner.text.toString()
        val amountText = binding.amountEditText.text.toString()

        if (category.isEmpty() || amountText.isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(this, getString(R.string.enter_valid_amount), Toast.LENGTH_SHORT).show()
            return
        }

        // Prevent duplicate category budgets
        if (budgets.any { it.category == category }) {
            Toast.makeText(this, getString(R.string.category_exists), Toast.LENGTH_SHORT).show()
            return
        }

        val uid = auth.currentUser?.uid ?: return
        val budget = Budget(
            category = category,
            budgetAmount = amount,
            spentAmount = 0.0,
            userId = uid
        )

        db.collection("budgets").add(budget)
            .addOnSuccessListener {
                clearForm()
                Toast.makeText(this, getString(R.string.budget_added), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add budget", Toast.LENGTH_SHORT).show()
            }
    }

    // Start editing a budget (populate form fields)
    private fun editBudget(budget: Budget) {
        editingBudget = budget
        binding.categorySpinner.setText(budget.category, false)
        binding.amountEditText.setText(budget.budgetAmount.toString())
        binding.addBudgetButton.text = getString(R.string.update_budget)
        binding.categorySpinner.isEnabled = false // Prevent category change when editing
    }

    // Update an existing budget in Firestore
    private fun updateBudget() {
        val budget = editingBudget ?: return
        val amountText = binding.amountEditText.text.toString()

        if (amountText.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_amount), Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(this, getString(R.string.enter_valid_amount), Toast.LENGTH_SHORT).show()
            return
        }

        val updatedBudget = budget.copy(budgetAmount = amount)
        db.collection("budgets").document(budget.id).set(updatedBudget)
            .addOnSuccessListener {
                clearForm()
                Toast.makeText(this, getString(R.string.budget_updated), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update budget", Toast.LENGTH_SHORT).show()
            }
    }

    // Show a confirmation dialog before deleting a budget
    private fun showDeleteConfirmation(budget: Budget) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_budget))
            .setMessage(getString(R.string.delete_budget_confirmation, budget.category))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                deleteBudget(budget)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    // Delete a budget from Firestore
    private fun deleteBudget(budget: Budget) {
        db.collection("budgets").document(budget.id).delete()
            .addOnSuccessListener {
                Toast.makeText(this, getString(R.string.budget_deleted), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete budget", Toast.LENGTH_SHORT).show()
            }
    }

    // Clear the form fields and reset the add/update button
    private fun clearForm() {
        binding.categorySpinner.text.clear()
        binding.amountEditText.text?.clear()
        binding.addBudgetButton.text = getString(R.string.add_budget)
        binding.categorySpinner.isEnabled = true
        editingBudget = null
    }
} 