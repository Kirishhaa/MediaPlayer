package com.example.mediaplayer.interfaces.audiointeraction

import com.example.mediaplayer.models.Audio
import com.example.mediaplayer.models.MetaData

interface AudioSender {
    fun sendPlayAudio(metadata: MetaData, audioList: List<Audio>)
    fun sendPauseAudio(metadata: MetaData)
    fun sendResumeAudio(metadata: MetaData)
    fun sendStopAudio()
}