package com.example.mediaplayer.intents

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.support.v4.media.session.MediaControllerCompat
import com.example.mediaplayer.service.MediaPlayerService

class IntentsHandler {

    private val audioBroadcastSender = AudioBroadcastSender()

    fun sendIntentsToService(
        context: Context,
        boundService: Boolean,
        serviceConnection: ServiceConnection
    ) {
        if (!boundService) {
            val intent = Intent(context, MediaPlayerService::class.java)
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            context.startService(intent)
        } else {
            audioBroadcastSender.sendPlayAudio(context)
        }
    }

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