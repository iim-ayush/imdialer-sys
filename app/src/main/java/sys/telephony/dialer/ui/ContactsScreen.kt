package sys.telephony.dialer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import sys.telephony.dialer.contacts.Contact
import sys.telephony.dialer.contacts.ContactsRepository

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

@Composable
fun ContactsScreen() {
    val context = LocalContext.current
    val repository = remember { ContactsRepository(context) }
    var contacts by remember { mutableStateOf<List<Contact>>(emptyList()) }
    var hasPermission by remember { 
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
        )
    }

    LaunchedEffect(Unit) {
        if (hasPermission) {
            contacts = repository.getContacts()
        }
    }

    if (!hasPermission) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Please grant Contacts permission in Settings")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(contacts) { contact ->
                ListItem(
                    headlineContent = { Text(contact.name) },
                    supportingContent = { Text(contact.phoneNumber) },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Divider()
            }
        }
    }
}
