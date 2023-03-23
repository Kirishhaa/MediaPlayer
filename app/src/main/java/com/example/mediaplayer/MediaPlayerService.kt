package com.example.mediaplayer

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.media.session.MediaSessionManager
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.PlaybackStatus

class MediaPlayerService: Service(), MediaPlayer.OnPreparedListener,
MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
AudioManager.OnAudioFocusChangeListener{

    private var currentIndex = -1
    private var audioList: ArrayList<Audio> = ArrayList()
    private var currentAudio: Audio? = null
    private var mediaPlayer:MediaPlayer? = null

    private var onGoingCall = false
    private var phoneStateListener: PhoneStateListener? = null
    private var telephonyManager: TelephonyManager? = null

    private var mediaSession: MediaSessionCompat? = null
    private var mediaSessionManager: MediaSessionManager? = null
    private var transportControls: MediaControllerCompat.TransportControls? = null

    private var notificationCreator: NotificationCreator? = null

    private lateinit var storage: StorageUtils

    companion object{
        const val ACTION_PLAY = "MEDIA_PLAYER_ACTION_PLAY"
        const val ACTION_PAUSE = "MEDIA_PLAYER_ACTION_PAUSE"
        const val ACTION_NEXT = "MEDIA_PLAYER_ACTION_NEXT"
        const val ACTION_PREVIOUS = "MEDIA_PLAYER_ACTION_PREVIOUS"
    }

    override fun onCreate() {
        super.onCreate()
        requestAudioFocus()
        callStateListener()
        registerPlayNewAudio()
        registerBecomingNoisyReceiver()
        notificationCreator = NotificationCreator(applicationContext)
        storage = StorageUtils(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        currentIndex = storage.readIndex()
        audioList = storage.readAudioList()


        if(currentIndex!=-1 && currentIndex<audioList.size) {
            currentAudio = audioList[currentIndex]
        } else stopSelf()

        if(!requestAudioFocus()) stopSelf()

        if(mediaSession==null) {
            initMediaSession()
            initMediaPlayer()
            notificationCreator?.createNotification(currentAudio!!, mediaSession!!, PlaybackStatus.PLAYING)
        }
        handlePlaybackUserInteraction(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        mediaSession?.release()
        notificationCreator?.removeNotification()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mediaPlayer!=null){
            stopAudio()
            mediaPlayer?.release()
        }
        removeAudioFocus()

        if(phoneStateListener != null){
            telephonyManager!!.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        }

        notificationCreator?.removeNotification()

        unregisterReceiver(becomingNoisyReceiver)
        unregisterReceiver(playNewAudioReceiver)

        storage.clearData()
    }

    private fun initMediaPlayer(){
        if(mediaPlayer==null) mediaPlayer = MediaPlayer()
        mediaPlayer!!.setOnCompletionListener(this)
        mediaPlayer!!.setOnErrorListener(this)
        mediaPlayer!!.setOnPreparedListener(this)
        mediaPlayer!!.reset()
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
        mediaPlayer!!.setDataSource(currentAudio!!.path)
        } catch (_:Exception) {
            stopSelf()
        }
        mediaPlayer!!.prepareAsync()
    }

    private fun playAudio(){
        if(!mediaPlayer!!.isPlaying){
            mediaPlayer!!.start()
        }
    }

    private fun playNextAudio(){
        if(currentIndex==audioList.size-1){
            currentIndex = 0
        } else currentIndex++
        storage.writeIndex(currentIndex)
        currentAudio = audioList[currentIndex]

        stopAudio()
        mediaPlayer?.reset()
        initMediaPlayer()
    }

    private fun playPrevAudio(){
        if(currentIndex==0){
            currentIndex = audioList.size-1
        } else currentIndex--
        storage.writeIndex(currentIndex)
        currentAudio = audioList[currentIndex]

        stopAudio()
        mediaPlayer?.reset()
        initMediaPlayer()
    }

    private fun stopAudio(){
        if(mediaPlayer!=null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
        }
    }

    private fun pauseAudio(){
        if(mediaPlayer!!.isPlaying){
            mediaPlayer!!.pause()
            currentIndex = mediaPlayer!!.currentPosition
        }
    }

    private fun resumeAudio(){
        if(!mediaPlayer!!.isPlaying){
            mediaPlayer!!.seekTo(currentIndex)
            mediaPlayer!!.start()
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        playAudio()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun onCompletion(mp: MediaPlayer?) {
        stopAudio()
        mediaPlayer?.reset()
        transportControls?.skipToNext()
    }


    override fun onBind(intent: Intent?): IBinder {
        return LocalBinder()
    }

    inner class LocalBinder: Binder() {
        fun getService(): MediaPlayerService{
            return this@MediaPlayerService
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when(focusChange){
            AudioManager.AUDIOFOCUS_GAIN -> {
                playAudio()
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                stopAudio()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                pauseAudio()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                if(mediaPlayer!!.isPlaying) mediaPlayer!!.setVolume(0.1f, 0.1f)
            }
        }
    }

    private fun requestAudioFocus(): Boolean {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            return true
        }
        return false
    }

    private fun removeAudioFocus(): Boolean{
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                (getSystemService(Context.AUDIO_SERVICE) as AudioManager).abandonAudioFocus(this)
    }

    private val becomingNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            pauseAudio()
            notificationCreator?.createNotification(currentAudio!!, mediaSession!!, PlaybackStatus.PAUSED)
        }
    }

    private fun registerBecomingNoisyReceiver() {
        val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(becomingNoisyReceiver, intentFilter)
    }

    private val playNewAudioReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            currentIndex = storage.readIndex()
            if(currentIndex==-1 || currentIndex>=audioList.size) stopSelf()
            else{
                currentAudio = audioList[currentIndex]
            }

            stopAudio()
            mediaPlayer?.reset()
            initMediaPlayer()
            updateMetaData()
            notificationCreator?.createNotification(currentAudio!!, mediaSession!!, PlaybackStatus.PLAYING)
        }
    }

    private fun registerPlayNewAudio(){
        val intentFilter = IntentFilter(MainActivity.BROADCAST_PLAY_NEW_AUDIO)
        registerReceiver(playNewAudioReceiver, intentFilter)
    }

    private fun callStateListener(){
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        phoneStateListener = object : PhoneStateListener(){
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                when(state){
                    TelephonyManager.CALL_STATE_RINGING -> {
                        pauseAudio()
                        onGoingCall = true
                    }
                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        pauseAudio()
                        onGoingCall = true
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        if(onGoingCall) {
                            resumeAudio()
                            onGoingCall = false
                        }
                    }
                }
            }
        }
        telephonyManager!!.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    private fun initMediaSession(){
        if(mediaSession!=null) return
        mediaSessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        mediaSession = MediaSessionCompat(applicationContext, "Audio Player")
        transportControls = mediaSession!!.controller!!.transportControls

        mediaSession!!.isActive = true

        updateMetaData()

        mediaSession!!.setCallback(object : MediaSessionCompat.Callback(){
            override fun onPlay() {
                super.onPlay()
                resumeAudio()
                notificationCreator?.createNotification(currentAudio!!, mediaSession!!, PlaybackStatus.PLAYING)
            }

            override fun onPause() {
                super.onPause()
                pauseAudio()
                notificationCreator?.createNotification(currentAudio!!, mediaSession!!, PlaybackStatus.PAUSED)
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                playNextAudio()
                updateMetaData()
                notificationCreator?.createNotification(currentAudio!!, mediaSession!!, PlaybackStatus.PLAYING)
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                playPrevAudio()
                updateMetaData()
                notificationCreator?.createNotification(currentAudio!!, mediaSession!!, PlaybackStatus.PLAYING)
            }

            override fun onStop() {
                super.onStop()
                stopAudio()
                notificationCreator?.removeNotification()
                stopSelf()
            }
        })
    }

    private fun updateMetaData(){
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.media_session_image)

        mediaSession!!.setMetadata(
            MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, bitmap)
                .putLong(MediaMetadata.METADATA_KEY_DURATION, audioList[currentIndex].duration)
                .putString(MediaMetadata.METADATA_KEY_TITLE, audioList[currentIndex].title)
                .build()
        )
    }

    private fun handlePlaybackUserInteraction(intent: Intent?) {
        if(intent==null || intent.action==null) return
        when(intent.action){
            ACTION_PLAY -> {
                transportControls?.play()
            }
            ACTION_PAUSE -> {
                transportControls?.pause()
            }
            ACTION_NEXT -> {
                transportControls?.skipToNext()
            }
            ACTION_PREVIOUS -> {
                transportControls?.skipToPrevious()
            }
        }
    }
}