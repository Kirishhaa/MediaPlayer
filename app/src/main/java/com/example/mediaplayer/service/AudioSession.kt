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
import com.example.mediaplayer.data.AudioDecoder
import com.example.mediaplayer.data.SongMetadata
import com.example.mediaplayer.data.PlaybackStatus
import com.example.mediaplayer.interfaces.AudioSessionInteraction

class AudioSession(
    private val context: Context,
    tag: String,
) : MediaSessionCompat(context, tag) {

    private val notificationCreator = NotificationCreator(context)
    private var audioPlayer: AudioPlayer? = null

    private var mediaSessionManager: MediaSessionManager? = null
    private var transportControls: MediaControllerCompat.TransportControls? = null
    private var obj: AudioSessionInteraction? = null

    fun setAudioPlayer(audioPlayer: AudioPlayer) {
        this.audioPlayer = audioPlayer
    }

    fun setCallback(obj: AudioSessionInteraction) {
        this.obj = obj
    }

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
                    audioPlayer?.currentAudio!!,
                    this@AudioSession,
                    PlaybackStatus.PLAYING
                )
                obj?.getCallback(
                    SongMetadata(
                        audioPlayer!!.currentIndex,
                        PlaybackStatus.PLAYING,
                        audioPlayer!!.isFavorite!!
                    )
                )
            }

            override fun onPause() {
                super.onPause()
                audioPlayer!!.pauseAudio()
                notificationCreator.createNotification(
                    audioPlayer?.currentAudio!!,
                    this@AudioSession,
                    PlaybackStatus.PAUSED
                )
                obj?.getCallback(
                    SongMetadata(
                        audioPlayer!!.currentIndex,
                        PlaybackStatus.PAUSED,
                        audioPlayer!!.isFavorite!!
                    )
                )
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                audioPlayer!!.playNextAudio()
                updateMetaData()
                notificationCreator.createNotification(
                    audioPlayer?.currentAudio!!,
                    this@AudioSession,
                    PlaybackStatus.PLAYING
                )
                obj?.getCallback(
                    SongMetadata(
                        audioPlayer!!.currentIndex,
                        PlaybackStatus.PLAYING,
                        audioPlayer!!.isFavorite!!
                    )
                )
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                audioPlayer!!.playPrevAudio()
                updateMetaData()
                notificationCreator.createNotification(
                    audioPlayer?.currentAudio!!,
                    this@AudioSession,
                    PlaybackStatus.PLAYING
                )
                obj?.getCallback(
                    SongMetadata(
                        audioPlayer!!.currentIndex,
                        PlaybackStatus.PLAYING,
                        audioPlayer!!.isFavorite!!
                    )
                )
            }

            override fun onStop() {
                super.onStop()
                audioPlayer!!.stopAudio()
                notificationCreator.removeNotification()
            }
        })
    }

    fun updateMetaData() {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.media_session_image)

        val entity = AudioDecoder.getAudioEntity(audioPlayer!!.currentAudio!!)

        this.setMetadata(
            MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, bitmap)
                .putLong(MediaMetadata.METADATA_KEY_DURATION, entity.duration)
                .putString(MediaMetadata.METADATA_KEY_TITLE, entity.title)
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