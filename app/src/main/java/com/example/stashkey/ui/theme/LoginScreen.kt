package com.example.stashkey.ui.theme

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.stashkey.vault.Vault
import com.example.stashkey.vault.VaultUtils

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: (username: String, password: String) -> Unit
) {
    val context = LocalContext.current
    var isSignUp by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isSignUp) "Sign Up" else "Login",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (username.isBlank() || password.isBlank()) {
                    message = "Username and password must not be empty"
                    return@Button
                }

                val file = VaultUtils.getVaultFile(context, username, password)
                val vault = Vault(password)

                if (isSignUp) {
                    if (file.exists()) {
                        message = "User already exists."
                    } else {
                        try {
                            val serialized = vault.serialize()
                            VaultUtils.saveVaultToFile(context, file, serialized)
                            onLoginSuccess(username, password)
                        } catch (e: Exception) {
                            message = "Error creating vault: ${e.message}"
                        }
                    }
                } else {
                    if (!file.exists()) {
                        message = "Invalid username or password"
                    } else {
                        try {
                            val data = VaultUtils.readVaultFromFile(file)
                            if (data != null) {
                                vault.deserialize(data)
                                onLoginSuccess(username, password)
                            } else {
                                message = "Could not read vault file"
                            }
                        } catch (e: Exception) {
                            message = "Invalid username or password"
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSignUp) "Create Account" else "Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {
            isSignUp = !isSignUp
            message = ""
        }) {
            Text(if (isSignUp) "Already have an account? Log in" else "Don't have an account? Sign up")
        }

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
