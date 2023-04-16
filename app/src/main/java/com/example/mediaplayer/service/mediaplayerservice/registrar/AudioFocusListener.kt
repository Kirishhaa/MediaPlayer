package com.example.mediaplayer.service.mediaplayerservice.registrar

import android.content.Context
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import com.example.mediaplayer.service.mediaplayerservice.AudioPlayer

class AudioFocusListener(private val audioPlayer: AudioPlayer) : OnAudioFocusChangeListener {

    var registerState: Boolean = false
        private set

    fun register(context: Context): Boolean {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val result = audioManager.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            registerState = true
            return true
        }
        registerState = false
        return false
    }

    fun unregister(context: Context): Boolean {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).abandonAudioFocus(
                    this
                )
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                audioPlayer.playAudio()
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                audioPlayer.stopAudio()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                audioPlayer.pauseAudio()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                if (audioPlayer.isPlaying) audioPlayer.setVolume(0.1f, 0.1f)
            }
        }
    }
}