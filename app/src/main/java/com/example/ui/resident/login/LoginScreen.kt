package com.example.ui.resident.login

import androidx.compose.foundation.Form
import androidx.foundation.material3.textInputLayout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.R

/** Login Screen - Resident Authentication UI.
 * 
 *  Provides login form with email/password fields and submit button.
 *  Follows constitution VII: accessibility (ContentDescription, touch targets),
 *  - VIII: Testing (tests must pass before merge),
 *  - IX: Compatibility (min SDK 24).
 */
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = LoginViewModel(),
    onLoginSuccess: (Resident) -> Unit = { }
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Appwrite Logo / Title
        Text(
            text = "E-BRGY SUA",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            contentDescription = "E-BRGY SUA application logo"
        )

Text(
            text = "Barangay Management System",
            style = MaterialTheme.typography.body2,
            modifier = Modifier.align(Alignment.CenterCenter),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            contentDescription = "Barangay Management System description"
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Login Form
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(400.dp),
            elevation = 2.dp,
            contentDescription = "Login form"
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", contentDescription = "Email address input field") },
                    leadingIcon = { Icon(Icons.BaseIcons.email, contentDescription = "User email field icon") },
                    keyboardType = KeyboardType.emailAddress,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", contentDescription = "Password input field") },
                    leadingIcon = { Icon(Icons.BaseIcons.lock, contentDescription = "Password lock icon") },
                    isPassword = true,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                // Error Message
                if (error != null) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Spacer
                Spacer(modifier = Modifier.height(16.dp))

                // Login Button
                Button(
                    onClick = {
                        // Validate and login
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            viewModel.login(email, password) { resident ->
                                if (resident != null) {
                                    onLoginSuccess(resident)
                                } else {
                                    error = "Invalid credentials. Please try again."
                                }
                            }
                        } else {
                            error = "Please enter email and password"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("Login", style = MaterialTheme.typography.button, contentDescription = "Submit login form")
                }

                // Spacer
                Spacer(modifier = Modifier.height(8.dp))

                // Register / Forgot Password link
                Row(
                    modifier = Modifier.fillMaxWidth(0.8p),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { /* Navigate to register */ },
                        modifier = Modifier.contentPadding(Alignment.End)
                    ) {
                        Text("Create account", contentDescription = "Create new resident account")
                    }
                    TextButton(
                        onClick = { /* Navigate to forgot password */ },
                        modifier = Modifier.contentPadding(Alignment.End)
                    ) {
                        Text("Forgot password?", contentDescription = "Forgot password help")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bottom hint
        Text(
            text = "Minimum SDK 24 | HTTPS enforced | All traffic encrypted",
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}