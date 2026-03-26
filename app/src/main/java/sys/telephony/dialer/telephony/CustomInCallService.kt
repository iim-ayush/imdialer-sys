package sys.telephony.dialer.telephony

import android.telecom.Call
import android.telecom.InCallService
import android.util.Log

class CustomInCallService : InCallService() {

    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            Log.d("CustomInCallService", "Call state changed: $state")
            CallStateManager.updateCall(call)
        }
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        Log.d("CustomInCallService", "Call added: ${call.details.handle}")
        call.registerCallback(callCallback)
        CallStateManager.updateCall(call)
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        Log.d("CustomInCallService", "Call removed")
        call.unregisterCallback(callCallback)
        CallStateManager.updateCall(null)
    }
}
