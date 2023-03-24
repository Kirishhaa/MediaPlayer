package com.example.mediaplayer.service

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.media.session.MediaSessionManager
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.example.mediaplayer.R
import com.example.mediaplayer.data.PlaybackStatus

class AudioSession(
    private val context: Context,
    tag: String,
    private val audioPlayer: AudioPlayer?,
    private val notificationCreator: NotificationCreator,
) : MediaSessionCompat(context, tag) {

    private var mediaSessionManager: MediaSessionManager? = null
    private var transportControls: MediaControllerCompat.TransportControls? = null

    fun initialize() {
        mediaSessionManager =
            context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        transportControls = this.controller!!.transportControls

        this.isActive = true

        updateMetaData()

        this.setCallback(object : Callback() {
            override fun onPlay() {
                super.onPlay()
                audioPlayer!!.resumeAudio()
                notificationCreator.createNotification(
                    audioPlayer.currentAudio!!,
                    this@AudioSession,
                    PlaybackStatus.PLAYING
                )
            }

            override fun onPause() {
                super.onPause()
                audioPlayer!!.pauseAudio()
                notificationCreator.createNotification(
                    audioPlayer.currentAudio!!,
                    this@AudioSession,
                    PlaybackStatus.PAUSED
                )
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                audioPlayer!!.playNextAudio()
                updateMetaData()
                notificationCreator.createNotification(
                    audioPlayer.currentAudio!!,
                    this@AudioSession,
                    PlaybackStatus.PLAYING
                )
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                audioPlayer!!.playPrevAudio()
                updateMetaData()
                notificationCreator.createNotification(
                    audioPlayer.currentAudio!!,
                    this@AudioSession,
                    PlaybackStatus.PLAYING
                )
            }

            override fun onStop() {
                super.onStop()
                audioPlayer!!.stopAudio()
                notificationCreator.removeNotification()
                (context as MediaPlayerService).stopSelf()
            }
        })
    }

    fun updateMetaData() {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.media_session_image)

        this.setMetadata(
            MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, bitmap)
                .putLong(MediaMetadata.METADATA_KEY_DURATION, audioPlayer!!.currentAudio!!.duration)
                .putString(MediaMetadata.METADATA_KEY_TITLE, audioPlayer.currentAudio!!.title)
                .build()
        )
    }

    fun handlePlaybackUserInteraction(intent: Intent?) {
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
}