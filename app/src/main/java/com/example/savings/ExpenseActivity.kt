package com.example.savings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.savings.data.Expense
import com.example.savings.data.ExpenseAdapter
import com.example.savings.data.ExpenseFormDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ExpenseActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: ExpenseAdapter
    private lateinit var expenses: MutableList<Expense>

    private var imagePickCallback: ((Uri) -> Unit)? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                imagePickCallback?.invoke(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        expenses = mutableListOf()

        adapter = ExpenseAdapter(
            expenses,
            onDelete = { deleteExpense(it.id) },
            onEdit = { showEditExpenseDialog(it) },
            onViewImage = { showImageDialog(it.imageUrl) }
        )

        val recyclerView = findViewById<RecyclerView>(R.id.expenseRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findViewById<com.google.android.material.button.MaterialButton>(R.id.addExpenseBtn).setOnClickListener {
            showAddExpenseDialog()
        }

        // Search logic
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterExpenses(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        loadExpenses()
    }

    private fun loadExpenses() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("expenses")
            .whereEqualTo("userId", uid)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                expenses.clear()
                for (doc in snapshot) {
                    val exp = doc.toObject(Expense::class.java).copy(id = doc.id)
                    expenses.add(exp)
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun deleteExpense(expenseId: String) {
        db.collection("expenses").document(expenseId)
            .delete()
            .addOnSuccessListener {
                // Expense deleted successfully
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }

    private fun showAddExpenseDialog() {
        val dialog = ExpenseFormDialog(
            context = this,
            expense = null,
            onSave = { db.collection("expenses").add(it) },
            onPickImage = { callback ->
                imagePickCallback = callback
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                imagePickerLauncher.launch(intent)
            }
        )
        dialog.show()
    }

    private fun showEditExpenseDialog(expense: Expense) {
        val dialog = ExpenseFormDialog(
            context = this,
            expense = expense,
            onSave = { updatedExpense ->
                db.collection("expenses").document(expense.id).set(updatedExpense)
            },
            onPickImage = { callback ->
                imagePickCallback = callback
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                imagePickerLauncher.launch(intent)
            }
        )
        dialog.show()
    }

    private fun showImageDialog(url: String?) {
        if (url.isNullOrEmpty()) return

        val dialog = android.app.AlertDialog.Builder(this)
            .create()

        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER

        Glide.with(this)
            .load(url)
            .into(imageView)

        dialog.setView(imageView)
        dialog.show()
    }

    private fun filterExpenses(query: String) {
        val filtered = if (query.isBlank()) {
            expenses
        } else {
            expenses.filter {
                it.description.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true) ||
                it.amount.toString().contains(query, ignoreCase = true)
            }
        }
        adapter.apply {
            (this as? com.example.savings.data.ExpenseAdapter)?.let {
                it.updateList(filtered)
            }
        }
    }
}
