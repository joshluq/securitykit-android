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
import androidx.compose.ui.unit.dp
import es.joshluq.securitykit.manager.SecuritykitManager
import es.joshluq.securitykit.showcase.ui.theme.ShowcaseTheme
import kotlinx.coroutines.launch

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
    securityManager: SecuritykitManager,
    modifier: Modifier = Modifier
) {
    var key by remember { mutableStateOf("my_secure_key") }
    var value by remember { mutableStateOf("") }
    var storedValue by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("SecurityKit Showcase", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = key,
            onValueChange = { key = it },
            label = { Text("Key") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            label = { Text("Value to Save") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                scope.launch {
                    securityManager.save(key, value)
                    value = "" // Clear input
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Securely")
        }

        HorizontalDivider()

        Button(
            onClick = {
                scope.launch {
                    securityManager.read(key).onSuccess {
                        storedValue = it
                    }.onFailure {
                        storedValue = "Error: ${it.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Read Secure Data")
        }

        if (storedValue != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Stored Value:", style = MaterialTheme.typography.labelLarge)
                    Text(storedValue ?: "Empty", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
