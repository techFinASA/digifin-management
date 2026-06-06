package com.example.digifin.ui.expense

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        },
        onDeleteExpense = { expenseId ->
            expenseViewModel.deleteExpense(expenseId)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseHistoryContent(
    expenses: List<Expense>,
    currencyFormatter: NumberFormat,
    onBack: () -> Unit,
    onExpenseClick: (String) -> Unit,
    onDeleteExpense: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") } // "All", "Debit", "Credit"
    
    // Month Filter State
    val currentCalendar = Calendar.getInstance()
    var selectedMonth by remember { mutableStateOf<Int>(currentCalendar.get(Calendar.MONTH)) }
    var expanded by remember { mutableStateOf(false) }

    val months = listOf(
        "January", "February", "March", "April", "May", "June", 
        "July", "August", "September", "October", "November", "December"
    )

    val filteredExpenses = expenses.filter {
        val matchesSearch = it.title.contains(searchQuery, ignoreCase = true) || 
                           it.category.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "Debit" -> it.type == "Expense" || it.type == ""
            "Credit" -> it.type == "Income"
            else -> true
        }
        
        val expenseCalendar = Calendar.getInstance().apply { timeInMillis = it.date }
        val matchesMonth = expenseCalendar.get(Calendar.MONTH) == selectedMonth &&
                          expenseCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)

        matchesSearch && matchesFilter && matchesMonth
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Transaction History",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp)),
                placeholder = { Text("Search transactions...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Month Selector & Year Display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    Surface(
                        onClick = { expanded = true },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = months[selectedMonth],
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.6f).background(MaterialTheme.colorScheme.surface)
                    ) {
                        months.forEachIndexed { index, month ->
                            DropdownMenuItem(
                                text = { Text(month) },
                                onClick = {
                                    selectedMonth = index
                                    expanded = false
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = if (selectedMonth == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = currentCalendar.get(Calendar.YEAR).toString(),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )
            }

            // Type Filter (Debit/Credit)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                val filters = listOf("All", "Debit", "Credit")
                filters.forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { selectedFilter = filter }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = filter,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filteredExpenses.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No transactions for this month", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(filteredExpenses) { expense ->
                        TransactionListItem(
                            expense = expense,
                            currencyFormatter = currencyFormatter,
                            onEdit = { onExpenseClick(expense.id) },
                            onDelete = { onDeleteExpense(expense.id) }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
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
            onExpenseClick = {},
            onDeleteExpense = {}
        )
    }
}
