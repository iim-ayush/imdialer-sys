package sys.telephony.dialer

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import sys.telephony.dialer.telephony.CallStateManager
import sys.telephony.dialer.ui.CallScreen
import sys.telephony.dialer.ui.ContactsScreen

class MainActivity : ComponentActivity() {

    private val roleRequestLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Default dialer granted
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Critical: Enable showing over lockscreen
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        checkDefaultDialerRole()

        setContent {
            DialerApp()
        }
    }

    private fun checkDefaultDialerRole() {
        val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
        val isRoleHeld = roleManager.isRoleHeld(RoleManager.ROLE_DIALER)
        
        if (!isRoleHeld) {
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
            roleRequestLauncher.launch(intent)
        }
    }
}

@Composable
fun DialerApp() {
    val activeCall by CallStateManager.activeCall.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    MaterialTheme {
        Scaffold(
            bottomBar = {
                if (activeCall == null) {
                    NavigationBar {
                        NavigationBarItem(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Contacts") },
                            label = { Text("Contacts") }
                        )
                        NavigationBarItem(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            icon = { Icon(Icons.Default.Call, contentDescription = "Dialer") },
                            label = { Text("Dialer") }
                        )
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                if (activeCall != null) {
                    CallScreen(call = activeCall!!)
                } else {
                    when (selectedTab) {
                        0 -> ContactsScreen()
                        1 -> Text("Dialer UI placeholder", modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}
