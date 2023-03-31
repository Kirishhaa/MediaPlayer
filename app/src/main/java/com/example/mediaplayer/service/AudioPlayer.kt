package com.example.mediaplayer.service

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.mediaplayer.data.StorageUtils
import com.example.mediaplayer.data.Audio
import kotlinx.coroutines.*

class AudioPlayer(
    context: Context,
    private var audioList: List<Audio>,
) : MediaPlayer() {
    private val storage = StorageUtils(context)
    private var listener: AudioPlayerListener? = null

    var currentAudio: Audio? = null
        private set
    var currentIndex = -1
        private set

    init{
        currentIndex = storage.readIndex()

        if (currentIndex != -1 && currentIndex < audioList.size) {
            currentAudio = audioList[currentIndex]
        }
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
            Log.d("AudioPlayer", "dataSource for currentAudio isn't right")
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
            start()
        }
    }

    fun playNewAudio() {
        currentIndex = storage.readIndex()
        audioList = storage.readAudioList()
        if (currentIndex == -1 || currentIndex >= audioList.size){
          Log.d("AudioPlayer", "cuurent index == -1 or < than audioList.size")
        } else {
            currentAudio = audioList[currentIndex]
        }
        stopAudio()
        reset()
        initialize()
    }
}