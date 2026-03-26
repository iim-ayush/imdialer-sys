package sys.telephony.dialer

import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
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
    ) { _ -> }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> 
        // Permissions handled
    }

    override fun onResume() {
        super.onResume()
        // Re-check permissions when returning to activity
        checkDefaultDialerRole()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        requestPermissions()
        checkDefaultDialerRole()

        setContent {
            DialerApp()
        }
    }

    private fun requestPermissions() {
        permissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.CALL_PHONE
            )
        )
    }

    private fun checkDefaultDialerRole() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            if (roleManager != null && !roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
                roleRequestLauncher.launch(intent)
            }
        } else {
            val telecomManager = getSystemService(TelecomManager::class.java)
            if (telecomManager != null && telecomManager.defaultDialerPackage != packageName) {
                val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                    .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
                roleRequestLauncher.launch(intent)
            }
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
