package com.example.mediaplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.mediaplayer.eventcontroller.intents.BroadcastSender
import com.example.mediaplayer.interfaces.AudioServiceCallback
import com.example.mediaplayer.models.MetaData

class RegistrarBroadcastsActivity(private val recipient: AudioServiceCallback) {

    companion object{
        const val AUDIO_TIME = "audio_time"
        const val AUDIO_METADATA = "audio_metadata"
    }

    fun registerAudioTimeFromService(context: Context) {
        val intentFilter = IntentFilter(BroadcastSender.BROADCAST_GET_AUDIO_TIME_FROM_SERVICE)
        context.registerReceiver(audioTimeFromServiceReceiver, intentFilter)
    }

    fun unregisterAudioTimeFromService(context: Context) {
        context.unregisterReceiver(audioTimeFromServiceReceiver)
    }

    fun unregisterMetaDataFromService(context: Context) {
        context.unregisterReceiver(audioMetaDataFromServiceReceiver)
    }

    fun registerAudioMetaDataFromService(context: Context) {
        val intentFilter = IntentFilter(BroadcastSender.BROADCAST_GET_METADATA_FROM_SERVICE)
        context.registerReceiver(audioMetaDataFromServiceReceiver, intentFilter)
    }

    private val audioTimeFromServiceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            recipient.getTimeAudioPlayer(intent?.extras?.getSerializable(AUDIO_TIME) as Pair<Int, Int>)
        }
    }

    private val audioMetaDataFromServiceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            recipient.getMetaDataFromService(intent?.extras?.getSerializable(AUDIO_METADATA) as MetaData)
        }
    }
}