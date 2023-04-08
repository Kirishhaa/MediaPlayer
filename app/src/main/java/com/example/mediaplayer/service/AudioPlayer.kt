package com.example.mediaplayer.service

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import com.example.mediaplayer.data.Storage
import com.example.mediaplayer.data.Audio

class AudioPlayer(
    context: Context,
) : MediaPlayer() {
    private var audioList: List<Audio>
    private val storage = Storage(context)
    private var listener: AudioPlayerListener? = null

    var currentAudio: Audio? = null
        private set
    var currentIndex = -1
        private set
    var isFavorite: Boolean? = null
        private set

    init {
        currentIndex = storage.readIndex()
        isFavorite = storage.readFavorite()
        audioList = if(storage.readFavorite()) storage.readFavoriteAudioList() else storage.readAllAudioList()

        if (currentIndex != -1 && currentIndex < audioList.size) {
            currentAudio = audioList[currentIndex]
        }
    }

    fun setListener(listener: AudioPlayerListener) {
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
        isFavorite = storage.readFavorite()
        audioList = if(storage.readFavorite()) storage.readFavoriteAudioList() else storage.readAllAudioList()
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
        isFavorite = storage.readFavorite()
        audioList = if(storage.readFavorite()) storage.readFavoriteAudioList() else storage.readAllAudioList()
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
        isFavorite = storage.readFavorite()
        audioList = if(storage.readFavorite()) storage.readFavoriteAudioList() else storage.readAllAudioList()
        if (currentIndex == -1 || currentIndex >= audioList.size) {
            Log.d("AudioPlayer", "cuurent index == -1 or < than audioList.size")
        } else {
            currentAudio = audioList[currentIndex]
        }
        stopAudio()
        reset()
        initialize()
    }
}