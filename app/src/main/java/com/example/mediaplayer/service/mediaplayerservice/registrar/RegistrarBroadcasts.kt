package com.example.mediaplayer.service.mediaplayerservice.registrar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import com.example.mediaplayer.eventcontroller.intents.AudioBroadcastSender
import com.example.mediaplayer.models.PlaybackStatus
import com.example.mediaplayer.service.mediaplayerservice.AudioPlayer
import com.example.mediaplayer.service.mediaplayerservice.AudioSession
import com.example.mediaplayer.service.mediaplayerservice.MediaPlayerService
import com.example.mediaplayer.service.mediaplayerservice.NotificationCreator

class RegistrarBroadcasts(
    private val audioPlayer: AudioPlayer,
    private val notificationCreator: NotificationCreator,
    private val audioSession: AudioSession,
) {

    fun unregister(context: Context) {
        context.run {
            unregisterReceiver(becomingNoisyReceiver)
            unregisterReceiver(pauseReceiver)
            unregisterReceiver(resumeReceiver)
            unregisterReceiver(stopReceiver)
            unregisterReceiver(playNewAudioReceiver)
        }
    }

    fun register(context: Context) {
        registerPauseReceiver(context)
        registerResumeReceiver(context)
        registerStopReceiver(context)
        registerPlayNewAudio(context)
        registerBecomingNoisyReceiver(context)
    }

    private val becomingNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            audioPlayer.pauseAudio()
            notificationCreator.createNotification(
                audioPlayer.currentAudio,
                audioSession,
                PlaybackStatus.PAUSED
            )
        }
    }

    private fun registerBecomingNoisyReceiver(context: Context) {
        val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        context.registerReceiver(becomingNoisyReceiver, intentFilter)
    }

    private val pauseReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            audioPlayer.pauseAudio()
            audioSession.updateMetaData()
            notificationCreator.createNotification(
                audioPlayer.currentAudio,
                audioSession,
                PlaybackStatus.PAUSED
            )
        }
    }

    private fun registerPauseReceiver(context: Context) {
        val intentFilter = IntentFilter(MediaPlayerService.ACTION_PAUSE)
        context.registerReceiver(pauseReceiver, intentFilter)
    }

    private val stopReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            audioPlayer.stopAudio()
            notificationCreator.removeNotification()
        }
    }

    private fun registerStopReceiver(context: Context) {
        val intentFilter = IntentFilter(MediaPlayerService.ACTION_STOP)
        context.registerReceiver(stopReceiver, intentFilter)
    }


    private val resumeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            audioPlayer.resumeAudio()
            audioSession.updateMetaData()
            notificationCreator.createNotification(
                audioPlayer.currentAudio,
                audioSession,
                PlaybackStatus.PLAYING
            )
        }
    }

    private fun registerResumeReceiver(context: Context) {
        val intentFilter = IntentFilter(MediaPlayerService.ACTION_RESUME)
        context.registerReceiver(resumeReceiver, intentFilter)
    }

    private val playNewAudioReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            audioPlayer.playNewAudio()
            audioSession.updateMetaData()
            notificationCreator.createNotification(
                audioPlayer.currentAudio,
                audioSession,
                PlaybackStatus.PLAYING
            )
        }
    }

    private fun registerPlayNewAudio(context: Context) {
        val intentFilter = IntentFilter(AudioBroadcastSender.BROADCAST_PLAY_NEW_AUDIO)
        context.registerReceiver(playNewAudioReceiver, intentFilter)
    }
}