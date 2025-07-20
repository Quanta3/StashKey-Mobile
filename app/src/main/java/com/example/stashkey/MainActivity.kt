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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.tooling.preview.Preview




import com.example.stashkey.ui.theme.StashKeyTheme
import com.example.stashkey.vault.Item
import com.example.stashkey.vault.Vault
import com.example.stashkey.vault.VaultUtils
import com.example.stashkey.ui.theme.LoginScreen
import java.io.File

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Vault : Screen("vault", "Vault", Icons.Filled.Lock)
    object Features : Screen("features", "Features", Icons.Filled.Star)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StashKeyTheme {
                var vault by remember { mutableStateOf<Vault?>(null) }

                if (vault == null) {
                    LoginScreen { username, password ->
                        val vaultFile = VaultUtils.getVaultFile(this, username, password)
                        val v = Vault(password)

                        val existingVaultData = VaultUtils.readVaultFromFile(vaultFile)
                        if (existingVaultData != null) {
                            try {
                                v.deserialize(existingVaultData)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            VaultUtils.saveVaultToFile(this, vaultFile, v.serialize())
                        }

                        vault = v
                    }
                } else {
                    MainApp(vault!!)
                }
            }
        }
    }
}

@Composable
fun MainApp(vault: Vault) {
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

@Composable
fun BottomNavigationBar(navController: NavController) {
    val screens = listOf(Screen.Vault, Screen.Features, Screen.Settings)
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
    var itemList by remember {
        mutableStateOf(List(vault.getSize()) { index -> vault.getLoginFromId(index) })
    }
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        ItemList(
            itemList = itemList,
            modifier = Modifier.fillMaxSize()
        )

        AddItemFloatingButton {
            showDialog = true
        }

        if (showDialog) {
            AddItemDialog(
                onAddItem = { username, email, password, note ->
                    val newItem = Item(username, email, password, note)
                    vault.AddUserLogin(newItem)
                    itemList = List(vault.getSize()) { index -> vault.getLoginFromId(index) }
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }
    }
}

@Composable
fun AddItemFloatingButton(onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("+")
        }
    }
}

@Composable
fun AddItemDialog(
    onAddItem: (String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
            Button(onClick = {
                onAddItem(username, email, password, note)
            }) {
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
    Text("Features Screen")
}

@Composable
fun SettingsScreen() {
    Text("Settings Screen")
}

@Composable
fun ItemCard(item: Item, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Username: ${item.getUsername()}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Email: ${item.getEmail()}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Password: ${item.getPassword()}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            if (item.getNote().isNotEmpty()) {
                Text("Note: ${item.getNote()}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun ItemList(itemList: List<Item>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(itemList) { item ->
            ItemCard(item = item)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewItemList() {
    val sampleList = listOf(
        Item("JohnDoe", "john@example.com", "1234", "Work credentials"),
        Item("JaneDoe", "jane@example.com", "abcd", "Personal email"),
        Item("BobSmith", "bob@example.com", "pass123", "Misc notes")
    )

    MaterialTheme {
        ItemList(itemList = sampleList)
    }
}
