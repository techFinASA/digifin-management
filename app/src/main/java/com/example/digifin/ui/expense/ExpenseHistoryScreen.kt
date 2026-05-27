package com.example.digifin.ui.expense

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.digifin.data.model.Expense
import com.example.digifin.data.model.User
import com.example.digifin.ui.dashboard.TransactionListItem
import com.example.digifin.ui.navigation.Screen
import com.example.digifin.ui.theme.DigifinTheme
import com.example.digifin.viewmodel.AuthViewModel
import com.example.digifin.viewmodel.ExpenseViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun ExpenseHistoryScreen(
    navController: NavController,
    expenseViewModel: ExpenseViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val expenses by expenseViewModel.expenses.collectAsState()
    val userData by authViewModel.userData.collectAsState()
    
    val currencyFormatter = remember(userData?.currency) {
        val user = userData
        val locale = when (user?.currency) {
            "EUR" -> Locale.FRANCE
            "GBP" -> Locale.UK
            "JPY" -> Locale.JAPAN
            else -> Locale.US
        }
        NumberFormat.getCurrencyInstance(locale).apply {
            if (user?.currency != null && user.currency.isNotEmpty()) {
                try {
                    currency = Currency.getInstance(user.currency)
                } catch (e: Exception) {}
            }
        }
    }

    ExpenseHistoryContent(
        expenses = expenses,
        currencyFormatter = currencyFormatter,
        onBack = { navController.popBackStack() },
        onExpenseClick = { expenseId ->
            navController.navigate(Screen.AddEditExpense.createRoute(expenseId))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseHistoryContent(
    expenses: List<Expense>,
    currencyFormatter: NumberFormat,
    onBack: () -> Unit,
    onExpenseClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredExpenses = expenses.filter {
        it.title.contains(searchQuery, ignoreCase = true) || 
        it.category.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search by title or category") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(filteredExpenses) { expense ->
                    TransactionListItem(expense, currencyFormatter) {
                        onExpenseClick(expense.id)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseHistoryPreview() {
    DigifinTheme {
        ExpenseHistoryContent(
            expenses = listOf(
                Expense(id = "1", title = "Lunch", amount = 15.0, category = "Food"),
                Expense(id = "2", title = "Uber", amount = 25.0, category = "Transport"),
                Expense(id = "3", title = "Shopping", amount = 50.0, category = "Shopping")
            ),
            currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US),
            onBack = {},
            onExpenseClick = {}
        )
    }
}
