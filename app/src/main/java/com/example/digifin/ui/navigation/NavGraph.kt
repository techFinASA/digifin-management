package com.example.digifin.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.digifin.ui.auth.*
import com.example.digifin.ui.dashboard.DashboardScreen
import com.example.digifin.ui.expense.AddEditExpenseScreen
import com.example.digifin.ui.expense.ExpenseHistoryScreen
import com.example.digifin.ui.profile.ProfileScreen
import com.example.digifin.ui.analysis.AnalysisScreen
import com.example.digifin.ui.about.AboutScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Welcome.route) {
            WelcomeScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController)
        }
        composable(
            route = Screen.AddEditExpense.route,
            arguments = listOf(navArgument("expenseId") { nullable = true })
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getString("expenseId")
            AddEditExpenseScreen(navController, expenseId)
        }
        composable(Screen.ExpenseHistory.route) {
            ExpenseHistoryScreen(navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
        composable(Screen.Analysis.route) {
            AnalysisScreen(navController)
        }
        composable(Screen.About.route) {
            AboutScreen(navController)
        }
    }
}
