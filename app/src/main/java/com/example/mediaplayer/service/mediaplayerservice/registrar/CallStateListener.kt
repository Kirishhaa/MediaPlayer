package com.example.mediaplayer.service.mediaplayerservice.registrar

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.example.mediaplayer.service.mediaplayerservice.AudioPlayer

class CallStateListener(private val audioPlayer: AudioPlayer) {

    private var onGoingCall = false

    private lateinit var telephonyManager: TelephonyManager

    private val phoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    audioPlayer.pauseAudio()
                    onGoingCall = true
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    audioPlayer.pauseAudio()
                    onGoingCall = true
                }
                TelephonyManager.CALL_STATE_IDLE -> {
                    if (onGoingCall) {
                        audioPlayer.resumeAudio()
                        onGoingCall = false
                    }
                }
            }
        }
    }

    fun register(context: Context) {
        telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    fun unregister() {
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
    }
}