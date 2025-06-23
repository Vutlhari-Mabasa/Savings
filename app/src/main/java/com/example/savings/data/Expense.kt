package com.example.savings.data

data class Expense(
    val id: String = "",
    val description: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val date: Long = 0L,
    val imageUrl: String? = null,
    val userId: String = ""
)