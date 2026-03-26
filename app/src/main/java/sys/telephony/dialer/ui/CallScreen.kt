package sys.telephony.dialer.ui

import android.telecom.Call
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sys.telephony.dialer.recorder.CallRecorder
import java.io.File

@Composable
fun CallScreen(call: Call) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    val callState by remember { derivedStateOf { call.state } }
    val callerName = call.details.handle?.schemeSpecificPart ?: "Unknown Caller"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = callerName,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = getCallStateString(callState),
                color = Color.LightGray,
                fontSize = 18.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (callState == Call.STATE_RINGING) {
                // Answer Button
                Button(
                    onClick = { call.answer(0) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                    shape = CircleShape,
                    modifier = Modifier.size(80.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Answer", tint = Color.White)
                }
            }

            // Reject/Hangup Button
            Button(
                onClick = { 
                    if (callState == Call.STATE_RINGING) call.reject(false, null)
                    else call.disconnect()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = CircleShape,
                modifier = Modifier.size(80.dp)
            ) {
                Icon(Icons.Default.Call, contentDescription = "End", tint = Color.White)
            }
        }

        // Recording Controls
        if (callState == Call.STATE_ACTIVE) {
            Button(
                onClick = {
                    if (isRecording) {
                        CallRecorder.stopRecording()
                        isRecording = false
                    } else {
                        val file = File(context.filesDir, "call_${System.currentTimeMillis()}.mp4")
                        CallRecorder.startRecording(file)
                        isRecording = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecording) Color.Red else Color.DarkGray
                ),
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Record"
                )
                Spacer(Modifier.width(8.dp))
                Text(if (isRecording) "Recording..." else "Record Call")
            }
        }
    }
}

private fun getCallStateString(state: Int): String = when (state) {
    Call.STATE_RINGING -> "Incoming Call..."
    Call.STATE_DIALING -> "Dialing..."
    Call.STATE_ACTIVE -> "Active Call"
    Call.STATE_DISCONNECTED -> "Disconnected"
    Call.STATE_HOLDING -> "On Hold"
    else -> "Connecting..."
}
