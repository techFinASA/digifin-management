package com.example.digifin.data.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

enum class Category(val title: String, val color: Color) {
    FOOD("Food", Color(0xFFE57373)),
    TRANSPORT("Transport", Color(0xFF81C784)),
    SHOPPING("Shopping", Color(0xFF64B5F6)),
    BILLS("Bills", Color(0xFFFFB74D)),
    ENTERTAINMENT("Entertainment", Color(0xFFBA68C8)),
    HEALTH("Health", Color(0xFF4DB6AC)),
    EDUCATION("Education", Color(0xFF7986CB)),
    TRAVEL("Travel", Color(0xFFAED581)),
    OTHERS("Others", Color(0xFF90A4AE))
}
