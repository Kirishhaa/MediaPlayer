package com.example.mediaplayer

import android.media.MediaPlayer
import android.util.Log
import com.example.mediaplayer.data.PlaybackStatus

class AudioPlayerListener(private val notificationCreator: NotificationCreator,
                          private val audioSession: AudioSession) : MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    override fun onPrepared(mp: MediaPlayer?) {
        (mp as AudioPlayer).playAudio()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        when(what){
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> {
                Log.d("SERVICE", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK")
            }
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
                Log.d("SERVICE", "MEDIA ERROR UNKNOWN")
            }
            MediaPlayer.MEDIA_ERROR_IO -> {
                Log.d("SERVICE", "IO EXCEPTION")
            }
        }
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
        (mp as AudioPlayer).stopAudio()
        mp.reset()
        mp.playNextAudio()
        audioSession.updateMetaData()
        notificationCreator.createNotification(mp.currentAudio!!, audioSession, PlaybackStatus.PLAYING)
    }
}