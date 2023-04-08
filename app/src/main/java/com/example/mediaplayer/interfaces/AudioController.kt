package com.example.mediaplayer.interfaces

import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.SongMetadata

interface AudioController {
    fun playAudio(songMetadata: SongMetadata, audioList: List<Audio>, isFavorite: Boolean)
    fun resumeAudio()
    fun pauseAudio()
    fun stopAudio()
    fun nextAudio()
}