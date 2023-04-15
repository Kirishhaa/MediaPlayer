package com.example.mediaplayer.interfaces.audiointeraction

import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.MetaData

interface AudioSender {
    fun sendPlayAudio(metadata: MetaData, audioList: List<Audio>)
    fun sendPauseAudio(metadata: MetaData)
    fun sendResumeAudio(metadata: MetaData)
    fun sendStopAudio()
}