package com.example.mediaplayer.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.example.mediaplayer.eventcontroller.intents.IntentsHandler
import com.example.mediaplayer.service.mediaplayerservice.MediaPlayerService

class ServiceWorker {
    var boundService = false
    private var musicService: MediaPlayerService?=null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaPlayerService.LocalBinder
            musicService = binder.getService()
            boundService = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            boundService = false
        }
    }

    fun sendIntentsToService(context: Context) {
        val intentsHandler = IntentsHandler()
        if (!boundService) {
            val intent = Intent(context, MediaPlayerService::class.java)
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            context.startService(intent)
        } else {
            intentsHandler.sendPlayAudio(context)
        }
    }
}