package sys.telephony.dialer.recorder

import android.media.MediaRecorder
import android.util.Log
import java.io.File
import java.io.IOException

object CallRecorder {
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false

    fun startRecording(outputFile: File) {
        if (isRecording) return

        try {
            mediaRecorder = MediaRecorder().apply {
                // VOICE_CALL requires CAPTURE_AUDIO_OUTPUT (privileged)
                setAudioSource(MediaRecorder.AudioSource.VOICE_CALL)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }
            isRecording = true
            Log.d("CallRecorder", "Recording started: ${outputFile.name}")
        } catch (e: Exception) {
            Log.e("CallRecorder", "Failed to start recording", e)
            stopRecording()
        }
    }

    fun stopRecording() {
        if (!isRecording) return
        
        try {
            mediaRecorder?.apply {
                stop()
                reset()
                release()
            }
        } catch (e: Exception) {
            Log.e("CallRecorder", "Error stopping recorder", e)
        } finally {
            mediaRecorder = null
            isRecording = false
            Log.d("CallRecorder", "Recording stopped")
        }
    }
}
