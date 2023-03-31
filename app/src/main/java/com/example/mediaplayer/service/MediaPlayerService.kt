package com.example.mediaplayer.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Binder
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.example.mediaplayer.MainActivity
import com.example.mediaplayer.data.Storage
import com.example.mediaplayer.data.PlaybackStatus
import com.example.mediaplayer.interfaces.AudioSessionInteraction

class MediaPlayerService : Service(),
    AudioManager.OnAudioFocusChangeListener {

    private var audioPlayer: AudioPlayer? = null
    private var audioPlayerListener: AudioPlayerListener? = null

    private var onGoingCall = false
    private var phoneStateListener: PhoneStateListener? = null
    private var telephonyManager: TelephonyManager? = null

    private lateinit var audioSession: AudioSession

    private var notificationCreator: NotificationCreator? = null

    private lateinit var storage: Storage

    companion object {
        const val ACTION_PLAY = "MEDIA_PLAYER_ACTION_PLAY"
        const val ACTION_PAUSE = "MEDIA_PLAYER_ACTION_PAUSE"
        const val ACTION_RESUME = "MEDIA_PLAYER_ACTION_RESUME"
        const val ACTION_NEXT = "MEDIA_PLAYER_ACTION_NEXT"
        const val ACTION_PREVIOUS = "MEDIA_PLAYER_ACTION_PREVIOUS"
    }

    override fun onCreate() {
        super.onCreate()
        requestAudioFocus()
        callStateListener()
        registerPlayNewAudio()
        registerBecomingNoisyReceiver()
        registerPauseReceiver()
        registerResumeReceiver()
        audioSession  = AudioSession(applicationContext, "AudioSession")
        notificationCreator = NotificationCreator(applicationContext)
        storage = Storage(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!requestAudioFocus()) stopSelf()

        if (!audioSession.isActive) {
            audioPlayer = AudioPlayer(applicationContext, storage.readAudioList())
            initMediaSession()
            audioPlayerListener = AudioPlayerListener(notificationCreator!!, audioSession)
            initMediaPlayer()
            notificationCreator?.createNotification(
                audioPlayer!!.currentAudio!!,
                audioSession,
                PlaybackStatus.PLAYING
            )
        }
        audioSession.handlePlaybackUserInteraction(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder {
        return LocalBinder()
    }

    inner class LocalBinder : Binder() {
        lateinit var session: AudioSession
        fun getService(obj: AudioSessionInteraction): MediaPlayerService {
            audioSession.setCallback(obj)
            session = audioSession
            return this@MediaPlayerService
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        audioSession.release()
        notificationCreator?.removeNotification()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (audioPlayer != null) {
            audioPlayer!!.stopAudio()
            audioPlayer!!.release()
        }
        removeAudioFocus()

        if (phoneStateListener != null) {
            telephonyManager!!.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        }

        notificationCreator?.removeNotification()

        unregisterReceiver(becomingNoisyReceiver)
        unregisterReceiver(playNewAudioReceiver)

        storage.clearData()
    }

    private fun initMediaPlayer() {
        if (audioPlayer == null) audioPlayer =
            AudioPlayer(applicationContext, storage.readAudioList())
        audioPlayer!!.setListener(audioPlayerListener!!)
        audioPlayer!!.initialize()
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                audioPlayer!!.playAudio()
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                audioPlayer!!.stopAudio()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                audioPlayer!!.pauseAudio()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                if (audioPlayer!!.isPlaying) audioPlayer!!.setVolume(0.1f, 0.1f)
            }
        }
    }

    private fun requestAudioFocus(): Boolean {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val result = audioManager.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true
        }
        return false
    }

    private fun removeAudioFocus(): Boolean {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                (getSystemService(Context.AUDIO_SERVICE) as AudioManager).abandonAudioFocus(this)
    }

    private val becomingNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            audioPlayer!!.pauseAudio()
            notificationCreator?.createNotification(
                audioPlayer!!.currentAudio!!,
                audioSession,
                PlaybackStatus.PAUSED
            )
        }
    }

    private fun registerBecomingNoisyReceiver() {
        val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(becomingNoisyReceiver, intentFilter)
    }

    private val pauseReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            audioPlayer!!.pauseAudio()
            audioSession.updateMetaData()
            notificationCreator?.createNotification(
                audioPlayer!!.currentAudio!!,
                audioSession,
                PlaybackStatus.PAUSED
            )
        }
    }

    private fun registerPauseReceiver(){
        val intentFilter = IntentFilter(ACTION_PAUSE)
        registerReceiver(pauseReceiver, intentFilter)
    }

    private val resumeReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            audioPlayer!!.resumeAudio()
            audioSession.updateMetaData()
            notificationCreator?.createNotification(
                audioPlayer!!.currentAudio!!,
                audioSession,
                PlaybackStatus.PLAYING
            )
        }
    }

    private fun registerResumeReceiver(){
        val intentFilter = IntentFilter(ACTION_RESUME)
        registerReceiver(resumeReceiver, intentFilter)
    }

    private val playNewAudioReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            audioPlayer!!.playNewAudio()
            audioSession.updateMetaData()
            notificationCreator?.createNotification(
                audioPlayer!!.currentAudio!!,
                audioSession,
                PlaybackStatus.PLAYING
            )
        }
    }

    private fun registerPlayNewAudio() {
        val intentFilter = IntentFilter(MainActivity.BROADCAST_PLAY_NEW_AUDIO)
        registerReceiver(playNewAudioReceiver, intentFilter)
    }

    private fun callStateListener() {
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                when (state) {
                    TelephonyManager.CALL_STATE_RINGING -> {
                        audioPlayer!!.pauseAudio()
                        onGoingCall = true
                    }
                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        audioPlayer!!.pauseAudio()
                        onGoingCall = true
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        if (onGoingCall) {
                            audioPlayer!!.resumeAudio()
                            onGoingCall = false
                        }
                    }
                }
            }
        }
        telephonyManager!!.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    private fun initMediaSession() {
        if (audioSession.isActive) return
        audioSession.setAudioPlayer(audioPlayer!!)
        audioSession.initialize()
    }

}