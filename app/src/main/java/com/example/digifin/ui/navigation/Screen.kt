package com.example.digifin.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Welcome : Screen("welcome")
    object Dashboard : Screen("dashboard")
    object AddEditExpense : Screen("add_edit_expense?expenseId={expenseId}") {
        fun createRoute(expenseId: String? = null) = "add_edit_expense?expenseId=$expenseId"
    }
    object ExpenseHistory : Screen("expense_history")
    object Profile : Screen("profile")
    object Analysis : Screen("analysis")
    object About : Screen("about")
}
