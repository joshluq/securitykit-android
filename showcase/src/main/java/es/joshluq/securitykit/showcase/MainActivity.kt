package es.joshluq.securitykit.showcase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import es.joshluq.securitykit.sdk.SecurityKit
import es.joshluq.securitykit.showcase.ui.theme.ShowcaseTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    val name: String,
    val email: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val securityManager = (application as ShowcaseApp).securityManager

        setContent {
            ShowcaseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SecurityShowcaseScreen(
                        securityManager = securityManager,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SecurityShowcaseScreen(
    securityManager: SecurityKit,
    modifier: Modifier = Modifier
) {
    var key by remember { mutableStateOf("user_profile") }
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var storedProfile by remember { mutableStateOf<UserProfile?>(null) }
    var statusMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("SecurityKit Showcase", style = MaterialTheme.typography.headlineMedium)
        Text("Generics & Object Storage Support", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Save Object (UserProfile)", fontWeight = FontWeight.Bold)
                
                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    label = { Text("Storage Key") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("User Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = userEmail,
                    onValueChange = { userEmail = it },
                    label = { Text("User Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        scope.launch {
                            val profile = UserProfile(id = "123", name = userName, email = userEmail)
                            securityManager.save(key, profile).onSuccess {
                                statusMessage = "Profile saved securely!"
                                userName = ""
                                userEmail = ""
                            }.onFailure {
                                statusMessage = "Save error: ${it.message}"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save User Profile Object")
                }
            }
        }

        HorizontalDivider()

        Button(
            onClick = {
                scope.launch {
                    securityManager.read<UserProfile>(key).onSuccess {
                        storedProfile = it
                        statusMessage = "Profile loaded and decrypted!"
                    }.onFailure {
                        storedProfile = null
                        statusMessage = "Read error: ${it.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Read Secure Object")
        }

        if (statusMessage.isNotEmpty()) {
            Text(statusMessage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        }

        if (storedProfile != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Decrypted Profile Data:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("ID: ${storedProfile?.id}")
                    Text("Name: ${storedProfile?.name}")
                    Text("Email: ${storedProfile?.email}")
                }
            }
        }
    }
}
