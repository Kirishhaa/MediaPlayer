package com.example.mediaplayer.service

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import com.example.mediaplayer.data.StorageUtils
import com.example.mediaplayer.data.Audio

class AudioPlayer(
    private val context: Context,
    private val audioList: List<Audio>,
) : MediaPlayer() {
    private val storage = StorageUtils(context)

    private var listener: AudioPlayerListener? = null

    var currentAudio: Audio? = null
        private set
    private var currentIndex = -1

    init{
        currentIndex = storage.readIndex()

        if (currentIndex != -1 && currentIndex < audioList.size) {
            currentAudio = audioList[currentIndex]
        } else (context as MediaPlayerService).stopSelf()
    }

    fun setListener(listener: AudioPlayerListener){
        this.listener = listener
    }

    fun initialize() {

        setOnErrorListener(listener)
        setOnPreparedListener(listener)
        setOnCompletionListener(listener)

        reset()

        setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            setDataSource(currentAudio!!.path)
        } catch (_: Exception) {
            (context as MediaPlayerService).stopSelf()
        }

        prepareAsync()
    }

    fun playAudio() {
        if (!isPlaying) {
            start()
        }
    }

    fun playNextAudio() {
        if (currentIndex == audioList.size - 1) {
            currentIndex = 0
        } else currentIndex++
        storage.writeIndex(currentIndex)
        currentAudio = audioList[currentIndex]

        stopAudio()
        reset()
        initialize()
    }

    fun playPrevAudio() {
        if (currentIndex == 0) {
            currentIndex = audioList.size - 1
        } else currentIndex--
        storage.writeIndex(currentIndex)
        currentAudio = audioList[currentIndex]

        stopAudio()
        reset()
        initialize()
    }

    fun stopAudio() {
        if (isPlaying) {
            stop()
        }
    }

    fun pauseAudio() {
        if (isPlaying) {
            pause()
        }
    }

    fun resumeAudio() {
        if (!isPlaying) {
            seekTo(currentIndex)
            start()
        }
    }

    fun playNewAudio() {
        currentIndex = storage.readIndex()
        if (currentIndex == -1 || currentIndex >= audioList.size) (context as MediaPlayerService).stopSelf()
        else {
            currentAudio = audioList[currentIndex]
        }

        stopAudio()
        reset()
        initialize()
    }
}