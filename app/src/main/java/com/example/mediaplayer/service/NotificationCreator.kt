package com.example.mediaplayer.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.example.mediaplayer.intents.IntentsFactory
import com.example.mediaplayer.R
import com.example.mediaplayer.data.models.Audio
import com.example.mediaplayer.data.AudioDecoder
import com.example.mediaplayer.data.models.PlaybackStatus

class NotificationCreator(
    private val context: Context) {

    private val intentsFactory = IntentsFactory()

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "MediaChannel"
    private val importance = NotificationManager.IMPORTANCE_DEFAULT
    private val channelName = "MediaPlayer"

    companion object {
        const val REQUEST_PLAY_PAUSE_CODE = 13
        const val REQUEST_PREVIOUS_CODE = 17
        const val REQUEST_NEXT_CODE = 11
        private const val NOTIFICATION_ID = 101
    }

    fun createNotification(
        audio: Audio,
        mediaSession: MediaSessionCompat?,
        status: PlaybackStatus,
    ) {

        val image: Int = when (status) {
            PlaybackStatus.PAUSED -> {
                R.drawable.ic_play_arrow
            }
            PlaybackStatus.PLAYING -> {
                R.drawable.ic_pause
            }
        }

        val entity = AudioDecoder.getAudioEntity(audio)

        createChannel()

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(entity.title)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(entity.bitmap)
            .setContentText("")
            .setAutoCancel(false)
            .setStyle(
                MediaStyle()
                    .setMediaSession(mediaSession?.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .addAction(
                R.drawable.ic_previous_arrow, "previous", intentsFactory.createPendingIntent(
                    context,
                    REQUEST_PREVIOUS_CODE, status
                )
            )
            .addAction(
                image,
                "play_pause",
                intentsFactory.createPendingIntent(context, REQUEST_PLAY_PAUSE_CODE, status)
            )
            .addAction(
                R.drawable.ic_next_arrow,
                "next",
                intentsFactory.createPendingIntent(context, REQUEST_NEXT_CODE, status)
            )

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun removeNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }
}