package com.example.mediaplayer.service.mediaplayerservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.mediaplayer.models.PlaybackStatus
import com.example.mediaplayer.service.mediaplayerservice.registrar.Registrar
import com.example.mediaplayer.storageutils.Storage

class MediaPlayerService : Service() {

    private lateinit var audioPlayerListener: AudioPlayerListener

    private lateinit var audioPlayer: AudioPlayer

    private lateinit var audioSession: AudioSession

    private lateinit var notificationCreator: NotificationCreator

    private lateinit var storage: Storage

    private lateinit var registrar: Registrar

    companion object {
        const val ACTION_PLAY = "MEDIA_PLAYER_ACTION_PLAY"
        const val ACTION_PAUSE = "MEDIA_PLAYER_ACTION_PAUSE"
        const val ACTION_RESUME = "MEDIA_PLAYER_ACTION_RESUME"
        const val ACTION_STOP = "MEDIA_PLAYER_ACTION_STOP"
        const val ACTION_NEXT = "MEDIA_PLAYER_ACTION_NEXT"
        const val ACTION_PREVIOUS = "MEDIA_PLAYER_ACTION_PREVIOUS"
    }

    override fun onCreate() {
        super.onCreate()
        storage = Storage(applicationContext)
        audioPlayer = AudioPlayer(applicationContext, storage)
        notificationCreator = NotificationCreator(applicationContext)
        audioSession =
            AudioSession(applicationContext, "AudioSession", audioPlayer, notificationCreator)
        audioPlayerListener = AudioPlayerListener(notificationCreator, audioSession)
        registrar = Registrar(audioPlayer, notificationCreator, audioSession)
        registrar.register(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!registrar.isAudioFocusRequest()) stopSelf()

        audioPlayer.setListener(audioPlayerListener)
        if (!audioSession.isActive) {
            audioPlayer.initialize()
            audioSession.initialize()
            notificationCreator.createNotification(
                audioPlayer.currentAudio,
                audioSession,
                PlaybackStatus.PLAYING
            )
        }
        audioSession.handlePlaybackUserInteraction(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder {
        return LocalBinder()
    }

    inner class LocalBinder : Binder() {
        fun getService(): MediaPlayerService {
            return this@MediaPlayerService
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        audioSession.release()
        notificationCreator.removeNotification()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayer.stopAudio()
        audioPlayer.release()
        notificationCreator.removeNotification()
        registrar.unregister(this)
        storage.clearData()
    }
}