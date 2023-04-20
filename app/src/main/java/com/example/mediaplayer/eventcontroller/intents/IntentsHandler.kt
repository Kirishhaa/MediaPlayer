package com.example.mediaplayer.eventcontroller.intents

import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaControllerCompat
import com.example.mediaplayer.models.MetaData
import com.example.mediaplayer.service.mediaplayerservice.MediaPlayerService

class IntentsHandler {

    private val broadcastSender = BroadcastSender()

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

    fun sendAudioTimeFromService(context: Context, data: Pair<Int, Int>) {
        broadcastSender.sendAudioTimeFromService(context, data)
    }

    fun sendMetaDataFromService(context: Context, metaData: MetaData) {
        broadcastSender.sendMetaDataFromService(context, metaData)
    }

    fun sendPlayAudio(context: Context) {
        broadcastSender.sendPlayAudio(context)
    }

    fun sendPauseAudio(context: Context) {
        broadcastSender.sendPauseAudio(context)
    }

    fun sendResumeAudio(context: Context) {
        broadcastSender.sendResumeAudio(context)
    }

    fun sendStopAudio(context: Context) {
        broadcastSender.sendStopAudio(context)
    }
}