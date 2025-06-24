package com.example.savings.data

data class Budget(
    val id: String = "",
    val category: String = "",
    val budgetAmount: Double = 0.0,
    val spentAmount: Double = 0.0,
    val userId: String = ""
) {
    val remainingAmount: Double
        get() = budgetAmount - spentAmount
    
    val isOverBudget: Boolean
        get() = spentAmount > budgetAmount
    
    val progressPercentage: Float
        get() = if (budgetAmount > 0) (spentAmount / budgetAmount * 100).toFloat() else 0f
} 