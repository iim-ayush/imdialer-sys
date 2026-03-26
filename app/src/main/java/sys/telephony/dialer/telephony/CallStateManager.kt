package sys.telephony.dialer.telephony

import android.telecom.Call
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object CallStateManager {
    private var realCall: Call? = null
    private val _activeCall = MutableStateFlow<CallData?>(null)
    val activeCall: StateFlow<CallData?> = _activeCall.asStateFlow()

    fun updateCall(call: Call?) {
        realCall = call
        if (call == null) {
            _activeCall.value = null
        } else {
            val number = try {
                call.details?.handle?.schemeSpecificPart ?: "Unknown"
            } catch (e: Exception) { "Unknown" }
            
            _activeCall.value = CallData(
                id = call.toString(),
                phoneNumber = number,
                state = call.state
            )
        }
    }

    fun answer() {
        if (_activeCall.value?.isMock == true) {
            _activeCall.value = _activeCall.value?.copy(state = Call.STATE_ACTIVE)
        } else {
            realCall?.answer(0)
        }
    }

    fun disconnect() {
        if (_activeCall.value?.isMock == true) {
            _activeCall.value = null
        } else {
            if (realCall?.state == Call.STATE_RINGING) {
                realCall?.reject(false, null)
            } else {
                realCall?.disconnect()
            }
        }
    }

    fun toggleMockCall() {
        if (_activeCall.value?.isMock == true) {
            _activeCall.value = null
        } else {
            _activeCall.value = CallData(
                id = "mock_123",
                phoneNumber = "+1 234 567 890",
                state = Call.STATE_RINGING, // Start as ringing for better debugging
                isMock = true
            )
        }
    }
}
