package com.example.digifin.data.model

import com.google.firebase.Timestamp

data class Expense(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val paymentType: String = "",
    val type: String = "Expense", // "Income" or "Expense"
    val date: Long = System.currentTimeMillis(),
    val notes: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
