package com.example.savings.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.savings.R
import java.text.NumberFormat
import java.util.*

// RecyclerView Adapter for displaying a list of budget goals
class BudgetAdapter(
    private var budgets: List<Budget>,
    private val onEditClick: (Budget) -> Unit,
    private val onDeleteClick: (Budget) -> Unit
) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    // ViewHolder for a single budget item
    class BudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryText: TextView = itemView.findViewById(R.id.categoryText)
        val budgetAmountText: TextView = itemView.findViewById(R.id.budgetAmountText)
        val spentAmountText: TextView = itemView.findViewById(R.id.spentAmountText)
        val remainingText: TextView = itemView.findViewById(R.id.remainingText)
        val statusText: TextView = itemView.findViewById(R.id.statusText)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        // Inflate the item layout for each budget
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_budget, parent, false)
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val budget = budgets[position]
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

        // Set category and formatted amounts
        holder.categoryText.text = budget.category
        holder.budgetAmountText.text = "Budget: R${String.format("%.2f", budget.budgetAmount)}"
        holder.spentAmountText.text = "Spent: R${String.format("%.2f", budget.spentAmount)}"
        holder.remainingText.text = "Remaining: R${String.format("%.2f", budget.remainingAmount)}"

        // Set progress bar to show spending progress
        holder.progressBar.progress = budget.progressPercentage.toInt()
        
        // Change colors and status text if over budget
        if (budget.isOverBudget) {
            holder.progressBar.progressTintList = android.content.res.ColorStateList.valueOf(
                holder.itemView.context.getColor(android.R.color.holo_red_light)
            )
            holder.remainingText.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_red_light)
            )
            holder.statusText.text = holder.itemView.context.getString(R.string.over_budget)
            holder.statusText.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_red_light)
            )
        } else {
            holder.progressBar.progressTintList = android.content.res.ColorStateList.valueOf(
                holder.itemView.context.getColor(android.R.color.holo_blue_light)
            )
            holder.remainingText.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_green_light)
            )
            holder.statusText.text = holder.itemView.context.getString(R.string.on_track)
            holder.statusText.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_green_light)
            )
        }

        // Set up edit and delete button listeners
        holder.editButton.setOnClickListener { onEditClick(budget) }
        holder.deleteButton.setOnClickListener { onDeleteClick(budget) }
    }

    override fun getItemCount(): Int = budgets.size

    // Update the list of budgets and refresh the view
    fun updateBudgets(newBudgets: List<Budget>) {
        budgets = newBudgets
        notifyDataSetChanged()
    }
} 