package com.example.mediaplayer.interfaces

import com.example.mediaplayer.data.Audio

interface AudioController {
    fun onPlayAudio(position: Int, list: List<Audio>)
    fun onResumeAudio()
    fun onPauseAudio()
}