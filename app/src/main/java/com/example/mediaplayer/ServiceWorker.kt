package com.example.mediaplayer

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import com.example.mediaplayer.intents.IntentsHandler
import com.example.mediaplayer.interfaces.AudioServiceCallback
import com.example.mediaplayer.service.MediaPlayerService

class ServiceWorker(interact: AudioServiceCallback, private val intentsHandler: IntentsHandler) {
    var boundService = false
    private lateinit var musicService: MediaPlayerService

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaPlayerService.LocalBinder
            musicService = binder.getService(interact)
            boundService = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            boundService = false
        }
    }

    fun sendIntentsToService(context: Context) {
        intentsHandler.sendIntentsToService(context, boundService, serviceConnection)
    }
}