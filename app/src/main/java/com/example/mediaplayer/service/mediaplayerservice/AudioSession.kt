package com.example.mediaplayer.service.mediaplayerservice

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import com.example.mediaplayer.R
import com.example.mediaplayer.dataoperations.AudioDecoder
import com.example.mediaplayer.models.PlaybackStatus
import com.example.mediaplayer.eventcontroller.intents.IntentsHandler

class AudioSession(
    private val context: Context,
    tag: String,
    private val audioPlayer: AudioPlayer,
    private val notificationCreator: NotificationCreator,
) : MediaSessionCompat(context, tag) {

    private var transportControls: MediaControllerCompat.TransportControls? = null
    private val intentsHandler = IntentsHandler()

    fun initialize() {
        transportControls = this.controller!!.transportControls
        this.isActive = true

        updateMetaData()

        this.setCallback(object : Callback() {
            override fun onPlay() {
                super.onPlay()
                audioPlayer.resumeAudio()
                onTriggerAudio(PlaybackStatus.PLAYING)
            }

            override fun onPause() {
                super.onPause()
                audioPlayer.pauseAudio()
                onTriggerAudio(PlaybackStatus.PAUSED)
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                audioPlayer.playNextAudio()
                updateMetaData()
                onTriggerAudio(PlaybackStatus.PLAYING)
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                audioPlayer.playPrevAudio()
                updateMetaData()
                onTriggerAudio(PlaybackStatus.PLAYING)
            }

            override fun onStop() {
                super.onStop()
                audioPlayer.stopAudio()
                notificationCreator.removeNotification()
            }
        })
    }

    private fun onTriggerAudio(playbackStatus: PlaybackStatus) {
        notificationCreator.createNotification(
            audioPlayer.currentAudio,
            this@AudioSession,
            playbackStatus
        )
    }

    fun updateMetaData() {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.media_session_image)
        val entity = AudioDecoder.getAudioEntity(audioPlayer.currentAudio)
        this.setMetadata(
            MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, bitmap)
                .putLong(MediaMetadata.METADATA_KEY_DURATION, entity.duration)
                .putString(MediaMetadata.METADATA_KEY_TITLE, entity.title)
                .build()
        )
    }

    fun handlePlaybackUserInteraction(intent: Intent?) {
        intentsHandler.handlePlaybackUserInteraction(intent, transportControls)
    }
}