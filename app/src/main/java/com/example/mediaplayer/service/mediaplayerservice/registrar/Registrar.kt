package com.example.mediaplayer.service.mediaplayerservice.registrar

import android.content.Context
import com.example.mediaplayer.service.mediaplayerservice.AudioPlayer
import com.example.mediaplayer.service.mediaplayerservice.AudioSession
import com.example.mediaplayer.service.mediaplayerservice.NotificationCreator

class Registrar(
    audioPlayer: AudioPlayer,
    notificationCreator: NotificationCreator,
    audioSession: AudioSession,
) {
    private val registrarBroadcasts =
        RegistrarBroadcasts(audioPlayer, notificationCreator, audioSession)
    private val audioFocusListener = AudioFocusListener(audioPlayer)
    private val callStateListener = CallStateListener(audioPlayer)

    fun isAudioFocusRequest() = audioFocusListener.registerState

    fun register(context: Context) {
        registrarBroadcasts.register(context)
        audioFocusListener.register(context)
        callStateListener.register(context)
    }

    fun unregister(context: Context) {
        registrarBroadcasts.unregister(context)
        audioFocusListener.unregister(context)
        callStateListener.unregister()
    }
}