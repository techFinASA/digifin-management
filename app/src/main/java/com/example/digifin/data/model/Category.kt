package com.example.digifin.data.model

import androidx.compose.ui.graphics.Color

enum class Category(val title: String, val color: Color, val isIncome: Boolean = false) {
    // Expense Categories
    FOOD("Food", Color(0xFFCC0033)),
    TRANSPORT("Transport", Color(0xFF81C784)),
    SHOPPING("Shopping", Color(0xFF64B5F6)),
    BILLS("Bills", Color(0xFFFFB74D)),
    ENTERTAINMENT("Entertainment", Color(0xFFBA68C8)),
    HEALTH("Health", Color(0xFF4DB6AC)),
    EDUCATION("Education", Color(0xFF7986CB)),
    TRAVEL("Travel", Color(0xFFAED581)),
    
    // Income Categories
    SALARY("Salary", Color(0xFF4CAF50), true),
    INVESTMENTS("Investments", Color(0xFF8BC34A), true),
    GIFT("Gift", Color(0xFFFFEB3B), true),
    
    // Shared
    OTHERS("Others", Color(0xFF90A4AE))
}
