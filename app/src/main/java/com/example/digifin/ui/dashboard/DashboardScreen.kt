package com.example.digifin.ui.dashboard

import androidx.compose.foundation.*
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.digifin.data.model.Category
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
        },
        onAnalysisClick = {
            navController.navigate(Screen.Analysis.route)
        },
        onAboutClick = {
            navController.navigate(Screen.About.route)
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
    onDeleteExpense: (String) -> Unit,
    onAnalysisClick: () -> Unit,
    onAboutClick: () -> Unit
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
            Surface(
                shadowElevation = 6.dp,
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.background
            ) {
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            "DigiFin", 
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        ) 
                    },
                    actions = {
                        Box(modifier = Modifier.padding(end = 8.dp)) {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
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
                                    leadingIcon = { Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary) }
                                )
                                DropdownMenuItem(
                                    text = { Text("About Us") },
                                    onClick = { showMenu = false; onAboutClick() },
                                    leadingIcon = { Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary) }
                                )
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                                DropdownMenuItem(
                                    text = { Text("Logout") },
                                    onClick = { showMenu = false; onLogoutClick() },
                                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = MaterialTheme.colorScheme.primary) },
                                    colors = MenuDefaults.itemColors(
                                        textColor = MaterialTheme.colorScheme.primary,
                                        leadingIconColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExpenseClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.Black,
                shape = RoundedCornerShape(18.dp),
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(28.dp))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "Hi, ${userData?.firstName?.replaceFirstChar { it.titlecase() } ?: "User"}",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-1).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Manage your finances effortlessly",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight(500)
                    )
                }
            }

            // Premium Balance Card
            item {
                BalanceCard(balance, currencyFormatter)
            }

            // Summary Stats
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
                        color = Color(0xFFCC0033),
                        currencyFormatter = currencyFormatter,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Spend Analysis
            if (expenses.any { it.type == "Expense" || it.type == "" }) {
                item {
                    SpendAnalysisSection(expenses, currencyFormatter, onAnalysisClick)
                }
            }

            // Recent Transactions Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "Recent History",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.sp
                        )
                    )
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onHistoryClick() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (expenses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No recent transactions",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(expenses.take(5)) { expense ->
                    TransactionListItem(
                        expense = expense,
                        currencyFormatter = currencyFormatter,
                        onEdit = { onExpenseClick(expense.id) },
                        onDelete = { onDeleteExpense(expense.id) }
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun SpendAnalysisSection(
    expenses: List<Expense>, 
    currencyFormatter: NumberFormat,
    onClick: () -> Unit
) {
    val expenseList = expenses.filter { it.type == "Expense" || it.type == "" }
    val totalSpend = expenseList.sumOf { it.amount }
    
    val categorySpends = expenseList
        .groupBy { it.category }
        .mapValues { it.value.sumOf { exp -> exp.amount } }
        .toList()
        .sortedByDescending { it.second }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Spend analysis",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pie Chart using Canvas
                Box(
                    modifier = Modifier.size(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(140.dp)) {
                        var startAngle = -90f
                        categorySpends.forEach { (catTitle, amount) ->
                            val sweepAngle = (amount / totalSpend * 360f).toFloat()
                            val category = Category.entries.find { it.title == catTitle } ?: Category.OTHERS
                            drawArc(
                                color = category.color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = 24.dp.toPx())
                            )
                            startAngle += sweepAngle
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = currencyFormatter.format(totalSpend).substringBefore("."),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(32.dp))
                
                // Details
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    categorySpends.take(4).forEach { (catTitle, amount) ->
                        val category = Category.entries.find { it.title == catTitle } ?: Category.OTHERS
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(category.color)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = catTitle,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = currencyFormatter.format(amount),
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BalanceCard(balance: Double, currencyFormatter: NumberFormat) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            Color(0xFFE5C76B), // SoftGold
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                    )
                )
                .padding(28.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.TopStart)) {
                Text(
                    text = "Total Balance",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Black.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currencyFormatter.format(balance),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1).sp
                    ),
                    color = Color.Black
                )
            }
            
            Row(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.History, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Updated just now",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Black.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = "DigiFin",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                ),
                color = Color.Black.copy(alpha = 0.2f),
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title, 
                style = MaterialTheme.typography.labelMedium, 
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currencyFormatter.format(amount),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f))
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
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(expense.category),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = expense.title.lowercase().replaceFirstChar { it.titlecase() },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Text(
                            text = expense.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                val isIncome = expense.type == "Income"
                Text(
                    text = (if (isIncome) "+" else "-") + currencyFormatter.format(expense.amount),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    ),
                    color = if (isIncome) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onEdit, 
                    modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.background, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Edit, 
                        contentDescription = "Edit", 
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onDelete, 
                    modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.background, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Delete, 
                        contentDescription = "Delete", 
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
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
            onDeleteExpense = {},
            onAnalysisClick = {},
            onAboutClick = {}
        )
    }
}
