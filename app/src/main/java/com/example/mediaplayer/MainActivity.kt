package com.example.mediaplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mediaplayer.data.models.Audio
import com.example.mediaplayer.data.models.MetaData
import com.example.mediaplayer.data.Storage
import com.example.mediaplayer.fragments.MenuFragment
import com.example.mediaplayer.intents.IntentsHandler
import com.example.mediaplayer.interfaces.AudioServiceCallback
import com.example.mediaplayer.interfaces.audiointeraction.AudioController
import com.example.mediaplayer.interfaces.metadatacontainer.MetaDataSource
import com.example.mediaplayer.interfaces.navigation.FragmentBackPressed

class MainActivity : AppCompatActivity(), AudioController, AudioServiceCallback {

    private lateinit var storage: Storage
    private lateinit var serviceWorker: ServiceWorker
    private val intentsHandler = IntentsHandler()
    private val permissionsHandler = PermissionsHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storage = Storage(applicationContext)
        serviceWorker = ServiceWorker(this, intentsHandler)
        serviceWorker.boundService = savedInstanceState?.getBoolean("isBound") ?: false
        permissionsHandler.requestPermissions(this)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.activity_fragment_container, MenuFragment())
                .commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isBound", serviceWorker.boundService)
    }

    override fun playAudio(
        metadata: MetaData,
        audioList: List<Audio>,
        isFavorite: Boolean,
        favoriteMap: Map<Int, Audio>?,
    ) {
        storage.writeIndex(metadata.currentPosition)
        storage.writeFavorite(isFavorite)
        if (isFavorite) {
            storage.writeFavoriteMap(favoriteMap!!)
        } else {
            storage.writeAllAudioList(audioList)
        }
        serviceWorker.sendIntentsToService(this)
    }

    override fun resumeAudio() {
        intentsHandler.sendResumeAudio(this)
    }

    override fun pauseAudio() {
        intentsHandler.sendPauseAudio(this)
    }

    override fun stopAudio() {
        intentsHandler.sendStopAudio(this)
    }

    override fun onBackPressed() {
        supportFragmentManager.fragments.getOrNull(0)?.apply {
            childFragmentManager.fragments.forEach {
                if (it is FragmentBackPressed) {
                    if (!it.onBackPressed()) super.onBackPressed()
                }
            }
        }
    }

    override fun getMetaDataFromService(metadata: MetaData) {
        supportFragmentManager.fragments.getOrNull(0)?.let {
            (it as MetaDataSource).updateMetaData(metadata)
        }
    }
}