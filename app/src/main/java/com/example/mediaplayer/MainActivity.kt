package com.example.mediaplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.MetaData
import com.example.mediaplayer.data.Storage
import com.example.mediaplayer.fragments.MenuFragment
import com.example.mediaplayer.interfaces.AudioSessionInteraction
import com.example.mediaplayer.interfaces.audiointeraction.AudioController
import com.example.mediaplayer.interfaces.navigation.FragmentBackPressed
import com.example.mediaplayer.interfaces.SourceFragment
import com.example.mediaplayer.service.MediaPlayerService

class MainActivity : AppCompatActivity(), AudioController, AudioSessionInteraction {

    private lateinit var musicService: MediaPlayerService
    private lateinit var storage: Storage

    private var boundService = false

    private val audioBroadcastSender = AudioBroadcastSender()

    private val permissionsHandler = PermissionsHandler()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaPlayerService.LocalBinder
            musicService = binder.getService(this@MainActivity)
            boundService = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            boundService = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storage = Storage(applicationContext)

        boundService = savedInstanceState?.getBoolean("isBound") ?: false

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
        outState.putBoolean("isBound", boundService)
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
        if (!boundService) {
            val intent = Intent(this, MediaPlayerService::class.java)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            startService(intent)
        } else {
            audioBroadcastSender.sendPlayAudio(this)
        }
    }

    override fun resumeAudio() {
        audioBroadcastSender.sendResumeAudio(this)
    }

    override fun pauseAudio() {
        audioBroadcastSender.sendPauseAudio(this)
    }

    override fun stopAudio() {
        audioBroadcastSender.sendStopAudio(this)
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
            (it as SourceFragment).updateMetaData(metadata)
        }
    }

}