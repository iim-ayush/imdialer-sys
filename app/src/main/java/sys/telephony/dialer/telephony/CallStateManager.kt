package sys.telephony.dialer.telephony

import android.telecom.Call
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object CallStateManager {
    private val _activeCall = MutableStateFlow<Call?>(null)
    val activeCall: StateFlow<Call?> = _activeCall.asStateFlow()

    private val _callState = MutableStateFlow(Call.STATE_DISCONNECTED)
    val callState: StateFlow<Int> = _callState.asStateFlow()

    fun updateCall(call: Call?) {
        _activeCall.value = call
        _callState.value = call?.state ?: Call.STATE_DISCONNECTED
    }

    fun updateState(state: Int) {
        _callState.value = state
    }
}
