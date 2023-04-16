package com.example.mediaplayer.eventcontroller.intents

import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaControllerCompat
import com.example.mediaplayer.service.mediaplayerservice.MediaPlayerService

class IntentsHandler {

    private val audioBroadcastSender = AudioBroadcastSender()

    fun handlePlaybackUserInteraction(
        intent: Intent?,
        transportControls: MediaControllerCompat.TransportControls?,
    ) {
        if (intent == null || intent.action == null) return
        when (intent.action) {
            MediaPlayerService.ACTION_PLAY -> {
                transportControls?.play()
            }
            MediaPlayerService.ACTION_PAUSE -> {
                transportControls?.pause()
            }
            MediaPlayerService.ACTION_NEXT -> {
                transportControls?.skipToNext()
            }
            MediaPlayerService.ACTION_PREVIOUS -> {
                transportControls?.skipToPrevious()
            }
        }
    }

    fun sendPlayAudio(context: Context) {
        audioBroadcastSender.sendPlayAudio(context)
    }

    fun sendPauseAudio(context: Context) {
        audioBroadcastSender.sendPauseAudio(context)
    }

    fun sendResumeAudio(context: Context) {
        audioBroadcastSender.sendResumeAudio(context)
    }

    fun sendStopAudio(context: Context) {
        audioBroadcastSender.sendStopAudio(context)
    }
}