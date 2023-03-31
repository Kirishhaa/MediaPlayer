package com.example.mediaplayer.interfaces

import com.example.mediaplayer.data.SongMetadata

interface AudioController {
    fun playAudio(songMetadata: SongMetadata)
    fun resumeAudio()
    fun pauseAudio()
}