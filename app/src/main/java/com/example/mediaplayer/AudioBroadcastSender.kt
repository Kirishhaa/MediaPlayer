package com.example.mediaplayer

import android.content.Context
import android.content.Intent
import com.example.mediaplayer.service.MediaPlayerService

class AudioBroadcastSender {

    companion object {
        const val BROADCAST_PLAY_NEW_AUDIO = "play_audio_changed_by_user"
        const val BROADCAST_CHANGE_TO_ALL_AUDIO = "change_to_all_audio"
    }

    fun changeToAllAudioList(context: Context) {
        val broadcastIntent = Intent(BROADCAST_CHANGE_TO_ALL_AUDIO)
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