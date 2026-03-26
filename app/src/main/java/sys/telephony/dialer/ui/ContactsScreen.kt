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

@Composable
fun ContactsScreen() {
    val context = LocalContext.current
    val repository = remember { ContactsRepository(context) }
    var contacts by remember { mutableStateOf<List<Contact>>(emptyList()) }

    LaunchedEffect(Unit) {
        contacts = repository.getContacts()
    }

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
