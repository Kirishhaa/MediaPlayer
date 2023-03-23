package com.example.mediaplayer

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.fragments.HorizontalFragment
import com.example.mediaplayer.fragments.MenuFragment
import com.example.mediaplayer.fragments.VerticalFragment

class MainActivity : AppCompatActivity() {
    private lateinit var musicService: MediaPlayerService
    private var boundService = false

    companion object {
        @JvmStatic
        val BROADCAST_PLAY_NEW_AUDIO = "play_audio_changed_by_user"

        @JvmStatic
        private val INITIAL_PERMS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MEDIA_CONTENT_CONTROL,
            Manifest.permission.READ_PHONE_STATE
        )
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaPlayerService.LocalBinder
            musicService = binder.getService()
            boundService = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            boundService = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        boundService = savedInstanceState?.getBoolean("isBound") ?: false
        requestPermissions(INITIAL_PERMS, 1)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.activity_fragment_container, MenuFragment.onInstance())
            .commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isBound", boundService)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (boundService) {
            unbindService(serviceConnection)
            musicService.stopSelf()
        }
        NotificationCreator(applicationContext).removeNotification()
    }


    fun playAudio(musicIndex: Int, audioList: ArrayList<Audio>) {
        if (!boundService) {
            val intent = Intent(this, MediaPlayerService::class.java)
            val storage = StorageUtils(applicationContext)
            storage.writeAudioList(audioList)
            storage.writeIndex(musicIndex)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            startService(intent)
        } else {
            val broadcastIntent = Intent(BROADCAST_PLAY_NEW_AUDIO)
            StorageUtils(applicationContext).writeIndex(musicIndex)
            sendBroadcast(broadcastIntent)
        }
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.fragments.getOrNull(0)
        if (currentFragment is MenuFragment) {
            currentFragment.childFragmentManager.fragments.forEach {
                if (it is VerticalFragment) {
                    currentFragment.childFragmentManager.beginTransaction()
                        .replace(R.id.menu_fragment_container, HorizontalFragment())
                        .commit()
                    return
                }
            }
        }
        super.onBackPressed()
    }
}