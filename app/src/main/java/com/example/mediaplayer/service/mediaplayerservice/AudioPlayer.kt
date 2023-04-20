package com.example.mediaplayer.service.mediaplayerservice

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import com.example.mediaplayer.models.Audio
import com.example.mediaplayer.storageutils.Storage
import com.example.mediaplayer.dataoperations.datacontroller.DataController
import com.example.mediaplayer.eventcontroller.intents.IntentsHandler
import com.example.mediaplayer.models.MetaData
import com.example.mediaplayer.models.PlaybackStatus
import com.example.mediaplayer.interfaces.AudioServiceCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class AudioPlayer(
    private val context: Context,
    private val storage: Storage,
) : MediaPlayer() {

    private val dataController = DataController()
    private lateinit var audioPlayerListener: AudioPlayerListener
    var audioList: List<Audio>
        private set
    var currentAudio: Audio
        private set
    var currentIndex = -1
        private set
    var isFavorite: Boolean by Delegates.notNull()
        private set

    init {
        currentIndex = storage.readIndex()
        isFavorite = storage.readFavorite()
        audioList = dataController.dso.setStartedAudioList(storage, isFavorite)
        currentAudio = dataController.dso.setStartedCurrentAudio(currentIndex, audioList)
    }

    fun setListener(listener: AudioPlayerListener) {
        this.audioPlayerListener = listener
        setOnErrorListener(audioPlayerListener)
        setOnPreparedListener(audioPlayerListener)
        setOnCompletionListener(audioPlayerListener)
    }

    fun initialize() {
        stopAudio()
        reset()

        currentIndex = storage.readIndex()
        isFavorite = storage.readFavorite()
        audioList = dataController.dso.setStartedAudioList(storage, isFavorite)
        currentAudio = dataController.dso.setStartedCurrentAudio(currentIndex, audioList)

        if (currentIndex != -1 && currentIndex < audioList.size) {
            currentAudio = audioList[currentIndex]
        }

        setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            setDataSource(currentAudio.path)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AudioPlayer", "dataSource for currentAudio isn't right")
        }

        prepareAsync()
    }

    fun playAudio() {
        if (!isPlaying) {
            start()
            callbackAudioPlayerInteraction()
            callbackTimeAudioPlayer()
        }
    }

    fun playNextAudio() {
        isFavorite = storage.readFavorite()
        audioList = dataController.dso.setStartedAudioList(storage, isFavorite)
        currentIndex = dataController.dso.setNextIndexInRangeAudioList(currentIndex, audioList)
        currentAudio = audioList[currentIndex]
        storage.writeIndex(currentIndex)
        initialize()
        callbackAudioPlayerInteraction()
    }

    fun playPrevAudio() {
        isFavorite = storage.readFavorite()
        audioList = dataController.dso.setStartedAudioList(storage, isFavorite)
        currentIndex = dataController.dso.setPreviousIndexInRangeAudioList(currentIndex, audioList)
        currentAudio = audioList[currentIndex]
        storage.writeIndex(currentIndex)
        initialize()
        callbackAudioPlayerInteraction()
    }

    private fun callbackAudioPlayerInteraction() {
        IntentsHandler().sendMetaDataFromService(
            context,
            MetaData(
                currentIndex,
                if (isPlaying) PlaybackStatus.PLAYING else PlaybackStatus.PAUSED,
                isFavorite
            )
        )
    }

    private fun callbackTimeAudioPlayer() {
        val intentsHandler = IntentsHandler()
        CoroutineScope(Dispatchers.IO).launch {
            while (isPlaying) {
                intentsHandler.sendAudioTimeFromService(context, Pair(currentPosition, duration))
            }
        }
    }

    fun stopAudio() {
        if (isPlaying) {
            stop()
            callbackAudioPlayerInteraction()
        }
    }

    fun pauseAudio() {
        if (isPlaying) {
            pause()
            callbackAudioPlayerInteraction()
        }
    }

    fun resumeAudio() {
        if (!isPlaying) {
            start()
            callbackAudioPlayerInteraction()
            callbackTimeAudioPlayer()
        }
    }

    fun playNewAudio() {
        isFavorite = storage.readFavorite()
        audioList = dataController.dso.setStartedAudioList(storage, isFavorite)
        currentIndex = storage.readIndex()
        currentAudio = dataController.dso.setStartedCurrentAudio(currentIndex, audioList)
        initialize()
        callbackAudioPlayerInteraction()
    }
}