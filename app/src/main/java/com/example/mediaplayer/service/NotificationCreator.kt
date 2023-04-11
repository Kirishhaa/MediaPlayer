package com.example.mediaplayer.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.example.mediaplayer.R
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.AudioDecoder
import com.example.mediaplayer.data.PlaybackStatus

class NotificationCreator(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "MediaChannel"
    private val importance = NotificationManager.IMPORTANCE_DEFAULT
    private val channelName = "MediaPlayer"
    companion object {
        private const val REQUEST_PLAY_PAUSE_CODE = 13
        private const val REQUEST_PREVIOUS_CODE = 17
        private const val REQUEST_NEXT_CODE = 11
        private const val NOTIFICATION_ID = 101
    }

    fun createNotification(audio: Audio, mediaSession: MediaSessionCompat?, status: PlaybackStatus){

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
            .setStyle(MediaStyle()
                .setMediaSession(mediaSession?.sessionToken)
                .setShowActionsInCompactView(0,1,2))
            .addAction(R.drawable.ic_previous_arrow, "previous", createPendingIntent(
                REQUEST_PREVIOUS_CODE, status))
            .addAction(image, "play_pause", createPendingIntent(REQUEST_PLAY_PAUSE_CODE, status))
            .addAction(R.drawable.ic_next_arrow, "next", createPendingIntent(REQUEST_NEXT_CODE, status))

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED)  return
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    private fun createPendingIntent(requestCode: Int, status: PlaybackStatus): PendingIntent? {
        val intent = Intent(context, MediaPlayerService::class.java)

        return when(requestCode){
            REQUEST_PLAY_PAUSE_CODE -> {
                if(status == PlaybackStatus.PAUSED) intent.action = MediaPlayerService.ACTION_PLAY
                else intent.action = MediaPlayerService.ACTION_PAUSE
                PendingIntent.getService(context, REQUEST_PLAY_PAUSE_CODE, intent, 0)
            }
            REQUEST_PREVIOUS_CODE -> {
                intent.action = MediaPlayerService.ACTION_PREVIOUS
                PendingIntent.getService(context, REQUEST_PREVIOUS_CODE, intent, 0)
            }
            REQUEST_NEXT_CODE -> {
                intent.action = MediaPlayerService.ACTION_NEXT
                PendingIntent.getService(context, REQUEST_NEXT_CODE, intent, 0)
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