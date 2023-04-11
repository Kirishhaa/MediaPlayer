package com.example.mediaplayer

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.SongMetadata
import com.example.mediaplayer.data.Storage
import com.example.mediaplayer.fragments.MenuFragment
import com.example.mediaplayer.interfaces.AudioController
import com.example.mediaplayer.interfaces.AudioSessionInteraction
import com.example.mediaplayer.interfaces.FragmentBackPressed
import com.example.mediaplayer.service.MediaPlayerService

class MainActivity : AppCompatActivity(), AudioController, AudioSessionInteraction {
    private lateinit var musicService: MediaPlayerService
    private var boundService = false

    companion object {
        @JvmStatic
        val BROADCAST_PLAY_NEW_AUDIO = "play_audio_changed_by_user"

        @JvmStatic
        private val INITIAL_PERMS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
        )
    }

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
        boundService = savedInstanceState?.getBoolean("isBound") ?: false
        setContentView(R.layout.activity_main)
        requestPermissions(INITIAL_PERMS, 1)
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
        songMetadata: SongMetadata,
        audioList: List<Audio>,
        isFavorite: Boolean,
        favoriteMap: Map<Int, Audio>?,
    ) {
        val storage = Storage(applicationContext)
        storage.writeIndex(songMetadata.currentPosition)
        storage.writeFavorite(isFavorite)
        if (isFavorite) storage.writeFavoriteMap(favoriteMap!!) else storage.writeAllAudioList(
            audioList
        )
        if (!boundService) {
            val intent = Intent(this, MediaPlayerService::class.java)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            startService(intent)
        } else {
            val broadcastIntent = Intent(BROADCAST_PLAY_NEW_AUDIO)
            sendBroadcast(broadcastIntent)
        }
    }

    override fun resumeAudio() {
        val broadcastIntent = Intent(MediaPlayerService.ACTION_RESUME)
        sendBroadcast(broadcastIntent)
    }

    override fun pauseAudio() {
        val broadcastIntent = Intent(MediaPlayerService.ACTION_PAUSE)
        sendBroadcast(broadcastIntent)
    }

    override fun stopAudio() {
        val broadcastIntent = Intent(MediaPlayerService.ACTION_STOP)
        sendBroadcast(broadcastIntent)
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

    override fun getCallback(songMetadata: SongMetadata) {
        supportFragmentManager.fragments.getOrNull(0)?.let {
            (it as MenuFragment).updateAllListMetaData(songMetadata)
        }
    }
}