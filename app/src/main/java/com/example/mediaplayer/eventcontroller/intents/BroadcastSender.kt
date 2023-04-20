package com.example.mediaplayer.eventcontroller.intents

import android.content.Context
import android.content.Intent
import com.example.mediaplayer.RegistrarBroadcastsActivity
import com.example.mediaplayer.models.MetaData
import com.example.mediaplayer.service.mediaplayerservice.MediaPlayerService

class BroadcastSender {

    companion object {
        const val BROADCAST_PLAY_NEW_AUDIO = "play_audio_changed_by_user"
        const val BROADCAST_GET_AUDIO_TIME_FROM_SERVICE = "get_audio_time"
        const val BROADCAST_GET_METADATA_FROM_SERVICE = "get_metadata"
    }

    fun sendMetaDataFromService(context: Context, metaData: MetaData) {
        val broadcastIntent = Intent(BROADCAST_GET_METADATA_FROM_SERVICE)
        broadcastIntent.putExtra(RegistrarBroadcastsActivity.AUDIO_METADATA, metaData)
        context.sendBroadcast(broadcastIntent)
    }

    fun sendAudioTimeFromService(context: Context, data: Pair<Int, Int>) {
        val broadcastIntent = Intent(BROADCAST_GET_AUDIO_TIME_FROM_SERVICE)
        broadcastIntent.putExtra(RegistrarBroadcastsActivity.AUDIO_TIME, data)
        context.sendBroadcast(broadcastIntent)
    }

    fun sendPlayAudio(context: Context) {
        val broadcastIntent = Intent(BROADCAST_PLAY_NEW_AUDIO)
        context.sendBroadcast(broadcastIntent)
    }

    fun sendResumeAudio(context: Context) {
        val broadcastIntent = Intent(MediaPlayerService.ACTION_RESUME)
        context.sendBroadcast(broadcastIntent)
    }

    fun sendPauseAudio(context: Context) {
        val broadcastIntent = Intent(MediaPlayerService.ACTION_PAUSE)
        context.sendBroadcast(broadcastIntent)
    }

    fun sendStopAudio(context: Context) {
        val broadcastIntent = Intent(MediaPlayerService.ACTION_STOP)
        context.sendBroadcast(broadcastIntent)
    }
}