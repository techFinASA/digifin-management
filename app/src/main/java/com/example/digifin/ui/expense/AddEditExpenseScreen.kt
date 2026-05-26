package com.example.digifin.ui.expense

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.digifin.data.model.Category
import com.example.digifin.data.model.Expense
import com.example.digifin.ui.theme.DigifinTheme
import com.example.digifin.viewmodel.ExpenseViewModel
import com.google.firebase.Timestamp

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
    var selectedCategory by remember { mutableStateOf(expense?.category ?: Category.OTHERS.title) }
    var notes by remember { mutableStateOf(expense?.notes ?: "") }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (expense == null) "Add Expense" else "Edit Expense") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Box {
                TextField(
                    value = selectedCategory,
                    onValueChange = { },
                    label = { Text("Category") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            "Drop down",
                            Modifier.clickable { expanded = true }
                        )
                    }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    Category.entries.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.title) },
                            onClick = {
                                selectedCategory = category.title
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val newExpense = Expense(
                        id = expense?.id ?: "",
                        title = title,
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        category = selectedCategory,
                        notes = notes,
                        date = expense?.date ?: System.currentTimeMillis(),
                        timestamp = Timestamp.now()
                    )
                    onSave(newExpense)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (expense == null) "Add" else "Update")
            }
            
            if (expense != null) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            }
        }
    }
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
