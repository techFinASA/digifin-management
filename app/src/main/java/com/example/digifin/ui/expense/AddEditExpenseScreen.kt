package com.example.digifin.ui.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.digifin.data.model.Category
import com.example.digifin.data.model.Expense
import com.example.digifin.data.model.PaymentType
import com.example.digifin.ui.theme.DigifinTheme
import com.example.digifin.util.getCategoryIcon
import com.example.digifin.util.getPaymentTypeIcon
import com.example.digifin.viewmodel.ExpenseViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddEditExpenseScreen(
    navController: NavController,
    expenseId: String?,
    viewModel: ExpenseViewModel = viewModel()
) {
    val expense = expenseId?.let { viewModel.getExpenseById(it) }

    AddEditExpenseContent(
        expense = expense,
        onBack = { navController.popBackStack() },
        onSave = { newExpense ->
            if (expenseId == null) {
                viewModel.addExpense(newExpense)
            } else {
                viewModel.updateExpense(newExpense)
            }
            navController.popBackStack()
        },
        onDelete = {
            if (expenseId != null) {
                viewModel.deleteExpense(expenseId)
                navController.popBackStack()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseContent(
    expense: Expense?,
    onBack: () -> Unit,
    onSave: (Expense) -> Unit,
    onDelete: () -> Unit
) {
    var title by remember { mutableStateOf(expense?.title ?: "") }
    var amount by remember { mutableStateOf(expense?.amount?.toString() ?: "0") }
    var selectedType by remember { mutableStateOf(expense?.type ?: "Expense") }
    var selectedCategory by remember { mutableStateOf(expense?.category ?: Category.OTHERS.title) }
    var selectedPaymentType by remember { mutableStateOf(expense?.paymentType ?: PaymentType.CASH.title) }
    var selectedDate by remember { mutableStateOf(expense?.date ?: System.currentTimeMillis()) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var paymentExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
    val dateFormatter = remember { SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()) }

    val scrollState = rememberScrollState()

    // Filter categories based on Income/Expense type
    val filteredCategories = remember(selectedType) {
        Category.entries.filter { 
            if (selectedType == "Income") it.isIncome || it == Category.OTHERS 
            else !it.isIncome || it == Category.OTHERS
        }
    }

    // Reset category if it's not in the filtered list
    LaunchedEffect(selectedType) {
        if (!filteredCategories.any { it.title == selectedCategory }) {
            selectedCategory = Category.OTHERS.title
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDate = datePickerState.selectedDateMillis ?: selectedDate
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this transaction? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (expense == null) "New Transaction" else "Edit Transaction",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (expense != null) {
                        IconButton(onClick = { showDeleteConfirmation = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Amount Input Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Amount",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = amount,
                        onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) amount = it },
                        textStyle = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        placeholder = {
                            Text(
                                "0.00",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Details Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Details",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 4.dp)
                )

                // Transaction Type (Debit/Credit)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val types = listOf("Expense", "Income")
                    types.forEach { type ->
                        val isSelected = selectedType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable { selectedType = type }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = type,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Category Selector
                Box {
                    ModernTextField(
                        value = selectedCategory,
                        onValueChange = { },
                        label = "Category",
                        icon = getCategoryIcon(selectedCategory),
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Default.KeyboardArrowDown, null)
                        }
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { categoryExpanded = true }
                    )
                    
                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        filteredCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.title) },
                                leadingIcon = {
                                    Icon(getCategoryIcon(category.title), null, tint = category.color)
                                },
                                onClick = {
                                    selectedCategory = category.title
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                // Date Selector
                Box {
                    ModernTextField(
                        value = dateFormatter.format(Date(selectedDate)),
                        onValueChange = { },
                        label = "Date",
                        icon = Icons.Default.CalendarToday,
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Default.DateRange, null)
                        }
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showDatePicker = true }
                    )
                }

                // Payment Type Selector
                Box {
                    ModernTextField(
                        value = selectedPaymentType,
                        onValueChange = { },
                        label = "Payment Type",
                        icon = getPaymentTypeIcon(selectedPaymentType),
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Default.KeyboardArrowDown, null)
                        }
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { paymentExpanded = true }
                    )
                    
                    DropdownMenu(
                        expanded = paymentExpanded,
                        onDismissRequest = { paymentExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        PaymentType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.title) },
                                leadingIcon = {
                                    Icon(getPaymentTypeIcon(type.title), null, tint = MaterialTheme.colorScheme.primary)
                                },
                                onClick = {
                                    selectedPaymentType = type.title
                                    paymentExpanded = false
                                }
                            )
                        }
                    }
                }

                // Title Input
                ModernTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = if (selectedType == "Income") "Description" else "What did you spend on?",
                    icon = Icons.Default.Edit
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Save Button
            Button(
                onClick = {
                    val newExpense = Expense(
                        id = expense?.id ?: "",
                        title = title,
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        category = selectedCategory,
                        paymentType = selectedPaymentType,
                        type = selectedType,
                        notes = expense?.notes ?: "",
                        date = selectedDate,
                        timestamp = Timestamp.now()
                    )
                    onSave(newExpense)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    if (expense == null) "Save Expense" else "Update Changes",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        minLines = minLines,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Preview(showBackground = true)
@Composable
fun AddExpensePreview() {
    DigifinTheme {
        AddEditExpenseContent(
            expense = null,
            onBack = {},
            onSave = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditExpensePreview() {
    DigifinTheme {
        AddEditExpenseContent(
            expense = Expense(title = "Rent", amount = 1000.0, category = "Bills"),
            onBack = {},
            onSave = {},
            onDelete = {}
        )
    }
}
