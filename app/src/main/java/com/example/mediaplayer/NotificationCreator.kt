package com.example.mediaplayer

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.PlaybackStatus

class NotificationCreator(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "MediaChannel"
    private val importance = NotificationManager.IMPORTANCE_DEFAULT
    private val channelName = "MediaPlayer"
    private val request_play_pause_code = 13
    private val request_previous_code = 17
    private val request_next_code = 11
    private val NOTIFICATION_ID = 101

    fun createNotification(audio: Audio, mediaSession: MediaSessionCompat, status: PlaybackStatus){

        val image: Int = when (status) {
            PlaybackStatus.PAUSED -> {
                R.drawable.ic_play_arrow
            }
            PlaybackStatus.PLAYING -> {
                R.drawable.ic_pause
            }
        }

        createChannel()

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(audio.title)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(audio.image)
            .setContentText("")
            .setAutoCancel(false)
            .setStyle(MediaStyle()
                .setMediaSession(mediaSession.sessionToken)
                .setShowActionsInCompactView(0,1,2))
            .addAction(R.drawable.ic_previous_arrow, "previous", createPendingIntent(request_previous_code, status))
            .addAction(image, "play_pause", createPendingIntent(request_play_pause_code, status))
            .addAction(R.drawable.ic_next_arrow, "next", createPendingIntent(request_next_code, status))

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED)  return
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    private fun createPendingIntent(requestCode: Int, status: PlaybackStatus): PendingIntent? {
        val intent = Intent(context, MediaPlayerService::class.java)

        return when(requestCode){
            request_play_pause_code -> {
                if(status == PlaybackStatus.PAUSED) intent.action = MediaPlayerService.ACTION_PLAY
                else intent.action = MediaPlayerService.ACTION_PAUSE
                PendingIntent.getService(context, request_play_pause_code, intent, 0)
            }
            request_previous_code -> {
                intent.action = MediaPlayerService.ACTION_PREVIOUS
                PendingIntent.getService(context, request_previous_code, intent, 0)
            }
            request_next_code -> {
                intent.action = MediaPlayerService.ACTION_NEXT
                PendingIntent.getService(context, request_next_code, intent, 0)
            }
            else -> null
        }
    }

    private fun createChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,channelName,importance)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun removeNotification(){
        notificationManager.cancel(NOTIFICATION_ID)
    }
}