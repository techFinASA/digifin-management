package com.example.digifin.ui.components

import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.digifin.data.model.Category
import com.example.digifin.data.model.Expense
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun CategoryPieChart(expenses: List<Expense>) {
    val categoryTotals = expenses.groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }

    val entries = categoryTotals.map { (category, total) ->
        PieEntry(total.toFloat(), category)
    }

    val colors = categoryTotals.keys.map { categoryName ->
        Category.entries.find { it.title == categoryName }?.color?.toArgb() ?: ColorTemplate.COLORFUL_COLORS[0]
    }

    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false
                isDrawHoleEnabled = true
                setHoleColor(android.graphics.Color.TRANSPARENT)
                legend.isEnabled = false
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        update = { chart ->
            val dataSet = PieDataSet(entries, "Expenses")
            dataSet.colors = colors
            dataSet.valueTextColor = android.graphics.Color.WHITE
            dataSet.valueTextSize = 12f

            val data = PieData(dataSet)
            chart.data = data
            chart.invalidate()
        }
    )
}
