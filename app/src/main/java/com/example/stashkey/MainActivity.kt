package com.example.stashkey

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.stashkey.ui.theme.StashKeyTheme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

import androidx.compose.ui.unit.dp



import androidx.compose.ui.unit.dp



import androidx.compose.foundation.layout.padding
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import java.io.*

import androidx.compose.ui.unit.dp
import com.example.stashkey.vault.UserLogin
import com.example.stashkey.vault.Vault

// Define the navigation destinations in a sealed class
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Vault : Screen("vault", "Vault", Icons.Filled.Lock)
    object Features : Screen("features", "Features", Icons.Filled.Star)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}


private fun getVaultFile(context: Context): File {
    return File(context.filesDir, "vault")
}


private fun saveVaultToFile(context: Context, data: ByteArray) {
    try {
        val file = getVaultFile(context)
        FileOutputStream(file).use { outputStream ->
            outputStream.write(data)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}



private fun readVaultFromFile(context: Context): ByteArray? {
    try {
        val file = getVaultFile(context)
        if (!file.exists()) return null

        val inputStream = FileInputStream(file)
        val data = inputStream.readBytes()
        inputStream.close()
        return data
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}



class MainActivity : ComponentActivity() {

    private lateinit var vault: Vault // Make vault a property

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check for existing vault file
        val existingVaultData = readVaultFromFile(this)

        if (existingVaultData == null) {
            // No existing vault file, create a new Vault
            vault = Vault("Password") // Create fresh Vault
            saveVaultToFile(this, vault.serialize()) // Save it
        } else {
            // Vault file exists, load from it
            vault = Vault("Password") // Create Vault object
            try {
                vault.deserialize(existingVaultData) // Deserialize the byte data into the vault
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        setContent {
            StashKeyTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    NavHost(
                        navController,
                        startDestination = Screen.Vault.route,
                        Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Vault.route) { VaultScreen(vault) }
                        composable(Screen.Features.route) { FeaturesScreen() }
                        composable(Screen.Settings.route) { SettingsScreen() }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        saveVault()
    }

    private fun saveVault() {
        try {
            saveVaultToFile(this, vault.serialize())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    // List of screens for bottom navigation
    val screens = listOf(Screen.Vault, Screen.Features, Screen.Settings)
    // Get the current back stack entry to determine the selected route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        screens.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun VaultScreen(vault: Vault) {
    var userLogins by remember {
        mutableStateOf(List(vault.getSize()) { index -> vault.getLoginFromId(index) })
    }
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        UserLoginList(
            userLogins = userLogins,
            modifier = Modifier.fillMaxSize()
        )

        AddUserFloatingButton {
            showDialog = true
        }

        if (showDialog) {
            AddUserDialog(
                onAddUser = { username, email, password, note ->
                    val newUser = UserLogin(username, email, password, note)
                    vault.AddUserLogin(newUser)
                    userLogins = List(vault.getSize()) { index -> vault.getLoginFromId(index) }
                    showDialog = false // close the dialog
                },
                onDismiss = { showDialog = false }
            )
        }
    }
}

@Composable
fun AddUserFloatingButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        FloatingActionButton(
            onClick = { onClick() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("+")
        }
    }
}

@Composable
fun AddUserDialog(
    onAddUser: (String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add New User") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") }
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") }
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") }
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAddUser(username, email, password, note)
                }
            ) {
                Text("Insert")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}




@Composable
fun FeaturesScreen() {
    Text(text = "Features Screen")
}

@Composable
fun SettingsScreen() {
    Text(text = "Settings Screen")
}



@Composable
fun UserLoginItem(userLogin: UserLogin, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Username: ${userLogin.getUsername()}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Email: ${userLogin.getEmail()}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Password: ${userLogin.getPassword()}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Only show note if it's not empty
            if (userLogin.getNote().isNotEmpty()) {
                Text(
                    text = "Note: ${userLogin.getNote()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}


@Composable
fun UserLoginList(userLogins: List<UserLogin>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(userLogins) { userLogin ->
            UserLoginItem(userLogin = userLogin)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserLoginList() {
    // Create sample data for preview.
    val sampleList = listOf(
        UserLogin("JohnDoe", "john.doe@example.com", "password123", "Sample note for John"),
        UserLogin("JaneDoe", "jane.doe@example.com", "secret456", "Sample note for Jane"),
        UserLogin("BobSmith", "bob.smith@example.com", "pass789", "Sample note for Bob")
    )

    MaterialTheme {
        UserLoginList(userLogins = sampleList)
    }
}