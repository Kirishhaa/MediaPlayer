package com.example.mediaplayer.service

import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.Storage
import kotlin.properties.Delegates

class AudioPlayer(
    private val storage: Storage,
) : MediaPlayer() {
    private var audioList: List<Audio>
    private lateinit var listener: AudioPlayerListener
    lateinit var currentAudio: Audio
        private set
    var currentIndex = -1
        private set
    var isFavorite: Boolean by Delegates.notNull()
        private set

    init {
        currentIndex = storage.readIndex()
        isFavorite = storage.readFavorite()
        audioList = if (storage.readFavorite()) {
            storage.readFavoriteMap().values.toList()
        } else {
            storage.readAllAudioList()
        }
        if (currentIndex != -1 && currentIndex < audioList.size) {
            currentAudio = audioList[currentIndex]
        }
    }

    fun initialize(listener: AudioPlayerListener) {
        this.listener = listener

        setOnErrorListener(listener)
        setOnPreparedListener(listener)
        setOnCompletionListener(listener)

        if (currentIndex != -1 && currentIndex < audioList.size) {
            currentAudio = audioList[currentIndex]
        }

        reset()

        setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            setDataSource(currentAudio.path)
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
        audioList =
            if (storage.readFavorite()) {
                storage.readFavoriteMap().values.toList()
            } else {
                storage.readAllAudioList()
            }
        if (currentIndex == audioList.size - 1) {
            currentIndex = 0
        } else currentIndex++
        storage.writeIndex(currentIndex)
        currentAudio = audioList[currentIndex]
        stopAudio()
        reset()
        initialize(listener)
    }

    fun playPrevAudio() {
        isFavorite = storage.readFavorite()
        audioList =
            if (storage.readFavorite()) {
                storage.readFavoriteMap().values.toList()
            } else {
                storage.readAllAudioList()
            }
        if (currentIndex == 0) {
            currentIndex = audioList.size - 1
        } else currentIndex--
        storage.writeIndex(currentIndex)
        currentAudio = audioList[currentIndex]
        stopAudio()
        reset()
        initialize(listener)
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
        audioList =
            if (storage.readFavorite()) {
                storage.readFavoriteMap().values.toList()
            } else {
                storage.readAllAudioList()
            }
        if (currentIndex == -1 || currentIndex >= audioList.size) {
            Log.d("AudioPlayer", "cuurent index == -1 or < than audioList.size")
        } else {
            currentAudio = audioList[currentIndex]
        }
        stopAudio()
        reset()
        initialize(listener)
    }
}