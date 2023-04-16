package com.example.mediaplayer.eventcontroller.intents

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.mediaplayer.models.PlaybackStatus
import com.example.mediaplayer.service.mediaplayerservice.MediaPlayerService
import com.example.mediaplayer.service.mediaplayerservice.NotificationCreator

class IntentsFactory {
    fun createPendingIntent(
        context: Context,
        requestCode: Int,
        status: PlaybackStatus,
    ): PendingIntent? {
        val intent = Intent(context, MediaPlayerService::class.java)
        return when (requestCode) {
            NotificationCreator.REQUEST_PLAY_PAUSE_CODE -> {
                if (status == PlaybackStatus.PAUSED) intent.action = MediaPlayerService.ACTION_PLAY
                else intent.action = MediaPlayerService.ACTION_PAUSE
                PendingIntent.getService(
                    context,
                    NotificationCreator.REQUEST_PLAY_PAUSE_CODE, intent, 0
                )
            }
            NotificationCreator.REQUEST_PREVIOUS_CODE -> {
                intent.action = MediaPlayerService.ACTION_PREVIOUS
                PendingIntent.getService(
                    context,
                    NotificationCreator.REQUEST_PREVIOUS_CODE, intent, 0
                )
            }
            NotificationCreator.REQUEST_NEXT_CODE -> {
                intent.action = MediaPlayerService.ACTION_NEXT
                PendingIntent.getService(context, NotificationCreator.REQUEST_NEXT_CODE, intent, 0)
            }
            else -> null
        }
    }
}