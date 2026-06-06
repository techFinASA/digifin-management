package com.example.digifin.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.digifin.R
import com.example.digifin.ui.navigation.Screen
import com.example.digifin.ui.theme.DigifinTheme
import com.example.digifin.viewmodel.AuthState
import com.example.digifin.viewmodel.AuthViewModel

data class CountryInfo(val name: String, val currency: String)

val countries = listOf(
    CountryInfo("United States", "USD"),
    CountryInfo("United Kingdom", "GBP"),
    CountryInfo("European Union", "EUR"),
    CountryInfo("India", "INR"),
    CountryInfo("Canada", "CAD"),
    CountryInfo("Australia", "AUD"),
    CountryInfo("Japan", "JPY"),
    CountryInfo("China", "CNY"),
    CountryInfo("Brazil", "BRL"),
    CountryInfo("South Africa", "ZAR")
)

@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Register.route) { inclusive = true }
            }
        }
    }

    RegisterContent(
        authState = authState,
        onRegister = { email, password, firstName, lastName, country, currency ->
            viewModel.register(email, password, firstName, lastName, country, currency)
        },
        onCheckVerification = {
            viewModel.checkVerificationStatus()
        },
        onLoginClick = { navController.navigate(Screen.Login.route) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterContent(
    authState: AuthState,
    onRegister: (String, String, String, String, String, String) -> Unit,
    onCheckVerification: () -> Unit,
    onLoginClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf(countries[0]) }
    var expanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val isVerificationSent = authState is AuthState.VerificationEmailSent

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.digifin_logo),
                contentDescription = "Logo",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isVerificationSent) "Check Your Email" else "Join Now",
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight(500)),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = if (isVerificationSent) "We've sent a verification link to $email" else "and start managing your finances",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(40.dp))

        if (!isVerificationSent) {
            ModernAuthTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = "First Name",
                icon = Icons.Default.Badge
            )
            Spacer(modifier = Modifier.height(16.dp))

            ModernAuthTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = "Last Name",
                icon = Icons.Default.Badge
            )
            Spacer(modifier = Modifier.height(16.dp))

            ModernAuthTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email Address",
                icon = Icons.Default.Email
            )
            Spacer(modifier = Modifier.height(16.dp))

            ModernAuthTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                icon = Icons.Default.Lock,
                isPassword = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                ModernAuthTextField(
                    value = selectedCountry.name,
                    onValueChange = {},
                    label = "Country",
                    icon = Icons.Default.Public,
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    countries.forEach { country ->
                        DropdownMenuItem(
                            text = { Text(country.name) },
                            onClick = {
                                selectedCountry = country
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            ModernAuthTextField(
                value = selectedCountry.currency,
                onValueChange = {},
                label = "Currency",
                icon = Icons.Default.Payments,
                readOnly = true,
                enabled = false
            )
        } else {
            // Icon to indicate email sent
            Icon(
                imageVector = Icons.Default.MarkEmailRead,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Please click the link in your email to verify your account, then come back here and click the button below.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        if (authState is AuthState.Loading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            Button(
                onClick = {
                    if (isVerificationSent) {
                        onCheckVerification()
                    } else {
                        onRegister(
                            email,
                            password,
                            firstName,
                            lastName,
                            selectedCountry.name,
                            selectedCountry.currency
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = if (isVerificationSent) "I've Verified My Email" else "Join Now",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
        
        TextButton(onClick = onLoginClick) {
            Text(if (isVerificationSent) "Go back to Login" else "Already have an account? Login")
        }

        if (authState is AuthState.Error) {
            Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun ModernAuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = trailingIcon,
        readOnly = readOnly,
        enabled = enabled,
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    DigifinTheme {
        RegisterContent(
            authState = AuthState.Idle,
            onRegister = { _, _, _, _, _, _ -> },
            onCheckVerification = {},
            onLoginClick = {}
        )
    }
}
