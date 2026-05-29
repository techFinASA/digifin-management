package com.example.digifin.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

fun getCategoryIcon(category: String): ImageVector {
    return when (category) {
        "Food" -> Icons.Default.Restaurant
        "Transport" -> Icons.Default.DirectionsCar
        "Shopping" -> Icons.Default.ShoppingBag
        "Bills" -> Icons.AutoMirrored.Filled.ReceiptLong
        "Entertainment" -> Icons.Default.Movie
        "Health" -> Icons.Default.MedicalServices
        "Education" -> Icons.Default.School
        "Travel" -> Icons.Default.Flight
        "Salary" -> Icons.Default.Payments
        "Investments" -> Icons.AutoMirrored.Filled.TrendingUp
        "Gift" -> Icons.Default.CardGiftcard
        else -> Icons.Default.Category
    }
}

fun getPaymentTypeIcon(paymentType: String): ImageVector {
    return when (paymentType) {
        "Cash" -> Icons.Default.Payments
        "Credit Card" -> Icons.Default.CreditCard
        "Debit Card" -> Icons.Default.CreditCard
        "UPI/Bank Transfer" -> Icons.Default.AccountBalance
        else -> Icons.Default.Payment
    }
}
