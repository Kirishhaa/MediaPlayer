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
    private val registrarBroadcastsService =
        RegistrarBroadcastsService(audioPlayer, notificationCreator, audioSession)
    private val audioFocusListener = AudioFocusListener(audioPlayer)
    private val callStateListener = CallStateListener(audioPlayer)

    fun isAudioFocusRequest() = audioFocusListener.registerState

    fun register(context: Context) {
        registrarBroadcastsService.register(context)
        audioFocusListener.register(context)
        callStateListener.register(context)
    }

    fun unregister(context: Context) {
        registrarBroadcastsService.unregister(context)
        audioFocusListener.unregister(context)
        callStateListener.unregister()
    }
}