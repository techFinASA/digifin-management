package com.example.digifin.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.digifin.data.model.User
import com.example.digifin.data.model.Expense
import com.example.digifin.ui.navigation.Screen
import com.example.digifin.ui.theme.DigifinTheme
import com.example.digifin.util.getCategoryIcon
import com.example.digifin.viewmodel.AuthViewModel
import com.example.digifin.viewmodel.ExpenseViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun DashboardScreen(
    navController: NavController,
    expenseViewModel: ExpenseViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val expenses by expenseViewModel.expenses.collectAsState()
    val userData by authViewModel.userData.collectAsState()
    
    DashboardContent(
        expenses = expenses,
        userData = userData,
        onProfileClick = { navController.navigate(Screen.Profile.route) },
        onLogoutClick = {
            authViewModel.logout()
            navController.navigate(Screen.Login.route) {
                popUpTo(0)
            }
        },
        onAddExpenseClick = { navController.navigate(Screen.AddEditExpense.createRoute()) },
        onHistoryClick = { navController.navigate(Screen.ExpenseHistory.route) },
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
fun DashboardContent(
    expenses: List<Expense>,
    userData: User?,
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onExpenseClick: (String) -> Unit,
    onDeleteExpense: (String) -> Unit
) {
    val totalExpense = expenses.filter { it.type == "Expense" || it.type == "" }.sumOf { it.amount }
    val totalIncome = expenses.filter { it.type == "Income" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense
    var showMenu by remember { mutableStateOf(false) }

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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Dashboard", 
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    ) 
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.Black,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.width(180.dp).background(MaterialTheme.colorScheme.surface)
                        ) {
                            DropdownMenuItem(
                                text = { Text("My Profile") },
                                onClick = { showMenu = false; onProfileClick() },
                                leadingIcon = { Icon(Icons.Default.Person, null) }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                            DropdownMenuItem(
                                text = { Text("Logout") },
                                onClick = { showMenu = false; onLogoutClick() },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null) },
                                colors = MenuDefaults.itemColors(
                                    textColor = MaterialTheme.colorScheme.primary,
                                    leadingIconColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = onAddExpenseClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.Black,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(32.dp))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    Text(
                        text = "Hi ${userData?.firstName?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } ?: "User"},",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Take care of your finance",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Modern Balance Card
            item {
                BalanceCard(balance, currencyFormatter)
            }

            // Income/Expense Summary
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SummaryItem(
                        title = "Income",
                        amount = totalIncome,
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        color = Color(0xFF4CAF50),
                        currencyFormatter = currencyFormatter,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryItem(
                        title = "Expenses",
                        amount = totalExpense,
                        icon = Icons.AutoMirrored.Filled.TrendingDown,
                        color = Color(0xFFF44336),
                        currencyFormatter = currencyFormatter,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Transactions Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onHistoryClick() }
                    )
                }
            }

            items(expenses.take(5)) { expense ->
                TransactionListItem(
                    expense = expense,
                    currencyFormatter = currencyFormatter,
                    onEdit = { onExpenseClick(expense.id) },
                    onDelete = { onDeleteExpense(expense.id) }
                )
            }
            
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun BalanceCard(balance: Double, currencyFormatter: NumberFormat) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.TopStart)) {
                Text(
                    text = "Total Balance",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Black.copy(alpha = 0.7f)
                )
                Text(
                    text = currencyFormatter.format(balance),
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.Black
                )
            }
            
            Text(
                text = "DigiFin Premium",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
fun SummaryItem(
    title: String,
    amount: Double,
    icon: ImageVector,
    color: Color,
    currencyFormatter: NumberFormat,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = currencyFormatter.format(amount),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun TransactionListItem(
    expense: Expense,
    currencyFormatter: NumberFormat,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = getCategoryIcon(expense.category),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = expense.title.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
                
                val isIncome = expense.type == "Income"
                Text(
                    text = (if (isIncome) "+" else "-") + currencyFormatter.format(expense.amount),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = if (isIncome) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                    Icon(
                        Icons.Default.Edit, 
                        contentDescription = "Edit", 
                        tint = Color.Black.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                    Icon(
                        Icons.Default.Delete, 
                        contentDescription = "Delete", 
                        tint = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    DigifinTheme {
        DashboardContent(
            expenses = listOf(
                Expense(id = "1", title = "NETFLIX SUBSCRIPTION", amount = 15.99, category = "Entertainment"),
                Expense(id = "2", title = "STARBUCKS COFFEE", amount = 5.50, category = "Food")
            ),
            userData = User(firstName = "Alex"),
            onProfileClick = {},
            onLogoutClick = {},
            onAddExpenseClick = {},
            onHistoryClick = {},
            onExpenseClick = {},
            onDeleteExpense = {}
        )
    }
}
