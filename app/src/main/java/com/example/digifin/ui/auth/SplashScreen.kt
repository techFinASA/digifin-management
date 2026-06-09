package com.example.digifin.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.digifin.R
import com.example.digifin.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(key1 = true) {
        delay(5000)
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Welcome.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.digifin_logo),
                contentDescription = "Digifin Logo",
                modifier = Modifier.size(width = 450.dp, height = 450.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Digifin",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC9922A)
            )
            Text(
                text = "SMART MONEY MANAGEMENT",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF3D1F00),
                letterSpacing = 3.sp
            )
        }
    }
}
