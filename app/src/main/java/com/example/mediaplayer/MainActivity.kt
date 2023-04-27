package com.example.mediaplayer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mediaplayer.eventcontroller.PermissionsHandler
import com.example.mediaplayer.models.Audio
import com.example.mediaplayer.models.MetaData
import com.example.mediaplayer.fragments.playerfragments.MenuFragment
import com.example.mediaplayer.eventcontroller.intents.IntentsHandler
import com.example.mediaplayer.fragments.PermissionsFragment
import com.example.mediaplayer.interfaces.ActivityNavigator
import com.example.mediaplayer.interfaces.AudioServiceCallback
import com.example.mediaplayer.interfaces.progressbar.ProgressBarSource
import com.example.mediaplayer.interfaces.audiointeraction.AudioController
import com.example.mediaplayer.interfaces.metadatacontainer.MetaDataSource
import com.example.mediaplayer.interfaces.navigation.FragmentBackPressed
import com.example.mediaplayer.service.ServiceWorker
import com.example.mediaplayer.storageutils.Storage

class MainActivity : AppCompatActivity(), AudioController, AudioServiceCallback, ActivityNavigator {

    private lateinit var storage: Storage
    private lateinit var serviceWorker: ServiceWorker
    private val intentsHandler = IntentsHandler()
    private val registrarBroadcastsActivity = RegistrarBroadcastsActivity(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storage = Storage(applicationContext)
        serviceWorker = ServiceWorker()
        serviceWorker.boundService = savedInstanceState?.getBoolean("isBound") ?: false
        registrarBroadcastsActivity.registerAudioMetaDataFromService(this)
        registrarBroadcastsActivity.registerAudioTimeFromService(this)
        if (savedInstanceState == null) {
            navigate(getStartedFragment())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isBound", serviceWorker.boundService)
    }

    override fun onDestroy() {
        super.onDestroy()
        registrarBroadcastsActivity.unregisterMetaDataFromService(this)
        registrarBroadcastsActivity.unregisterAudioTimeFromService(this)
    }

    private fun getStartedFragment(): Fragment {
        val permissionsHandler = PermissionsHandler()
        return if (permissionsHandler.isReadExternalStorageGranted(applicationContext)) {
            MenuFragment()
        } else {
            PermissionsFragment()
        }
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
        supportFragmentManager.fragments.getOrNull(0).let { menuFragment ->
            menuFragment?.childFragmentManager?.fragments?.forEach { fragmentBackPressed ->
                if (fragmentBackPressed is FragmentBackPressed) {
                    if (!fragmentBackPressed.onBackPressed()) super.onBackPressed()
                }
            }
        }
    }

    override fun getMetaDataFromService(metadata: MetaData) {
        supportFragmentManager.fragments.getOrNull(0).let {
            (it as MetaDataSource).updateMetaData(metadata)
        }
    }

    override fun getTimeAudioPlayer(data: Pair<Int, Int>) {
        Handler(Looper.getMainLooper()).post {
            supportFragmentManager.fragments.forEach {
                if(it is ProgressBarSource) it.updateAudioCurrentTime(data)
            }
        }
    }

    override fun navigate(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.activity_fragment_container, fragment)
            .commit()
    }
}