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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Hi, ${userData?.firstName?.replaceFirstChar { it.titlecase() } ?: "User"}",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-1.5).sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        ) {
                            Text(
                                text = "PREMIUM",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Your financial health at a glance",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
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
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(28.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Spend Analysis",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pie Chart using Canvas
                Box(
                    modifier = Modifier.size(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(150.dp)) {
                        var startAngle = -90f
                        categorySpends.forEach { (catTitle, amount) ->
                            val sweepAngle = (amount / totalSpend * 360f).toFloat()
                            val category = Category.entries.find { it.title == catTitle } ?: Category.OTHERS
                            drawArc(
                                color = category.color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = 28.dp.toPx())
                            )
                            startAngle += sweepAngle
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TOTAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Text(
                            text = currencyFormatter.format(totalSpend).substringBefore("."),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(36.dp))
                
                // Details
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    categorySpends.take(4).forEach { (catTitle, amount) ->
                        val category = Category.entries.find { it.title == catTitle } ?: Category.OTHERS
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(category.color)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = catTitle,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = currencyFormatter.format(amount),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
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
            .height(200.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFD4AF37), // Gold
                            Color(0xFFF7E7CE), // Champagne
                            Color(0xFFE5C76B)  // SoftGold
                        )
                    )
                )
        ) {
            // Subtle watermark
            Text(
                text = "DigiFin",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.Black.copy(alpha = 0.03f)
                ),
                modifier = Modifier.align(Alignment.Center).rotate(-15f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Balance",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Black.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color.Black.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currencyFormatter.format(balance),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-2).sp
                        ),
                        color = Color.Black
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.History, null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Live Update",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black
                            )
                        }
                    }
                    
                    Text(
                        text = "DigiFin Gold",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color = Color.Black.copy(alpha = 0.3f)
                    )
                }
            }
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
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title, 
                style = MaterialTheme.typography.labelMedium, 
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = currencyFormatter.format(amount),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
    val dateFormatter = remember { java.text.SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = getCategoryIcon(expense.category),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.title.lowercase().replaceFirstChar { it.titlecase() },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = dateFormatter.format(Date(expense.date)),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                val isIncome = expense.type == "Income"
                Text(
                    text = (if (isIncome) "+" else "-") + currencyFormatter.format(expense.amount),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1).sp
                    ),
                    color = if (isIncome) Color(0xFF4CAF50) else Color(0xFFCC0033)
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Surface(
                        onClick = onEdit,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Edit, 
                                contentDescription = "Edit", 
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Surface(
                        onClick = onDelete,
                        shape = CircleShape,
                        color = Color(0xFFCC0033).copy(alpha = 0.08f),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Delete, 
                                contentDescription = "Delete", 
                                tint = Color(0xFFCC0033).copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
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
