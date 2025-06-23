package com.example.savings.data

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.savings.R

class ExpenseAdapter(
    private val expenses: List<Expense>,
    private val onDelete: (Expense) -> Unit,
    private val onEdit: (Expense) -> Unit,
    private val onViewImage: (Expense) -> Unit // ✅ NEW
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val desc: TextView = view.findViewById(R.id.tvDescription)
        val amount: TextView = view.findViewById(R.id.tvAmount)
        val category: TextView = view.findViewById(R.id.tvCategory)
        val date: TextView = view.findViewById(R.id.tvDate)
        val editBtn: Button = view.findViewById(R.id.btnEdit)
        val deleteBtn: Button = view.findViewById(R.id.btnDelete)
        val imgReceipt: ImageView = view.findViewById(R.id.imgReceipt) // ✅ NEW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val item = expenses[position]

        holder.desc.text = item.description
        holder.amount.text = "R%.2f".format(item.amount)
        holder.category.text = item.category
        holder.date.text = DateFormat.format("dd MMM yyyy", item.date)

        holder.editBtn.setOnClickListener { onEdit(item) }
        holder.deleteBtn.setOnClickListener { onDelete(item) }

        // ✅ Load image if available
        if (!item.imageUrl.isNullOrBlank()) {
            holder.imgReceipt.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .into(holder.imgReceipt)

            holder.imgReceipt.setOnClickListener {
                onViewImage(item)
            }
        } else {
            holder.imgReceipt.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = expenses.size
}
