package com.example.mediaplayer.interfaces

import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.SongMetadata

interface AudioController {
    fun playAudio(songMetadata: SongMetadata, audioList: List<Audio>, isFavorite: Boolean, favoriteMap: Map<Int, Audio>?=null)
    fun resumeAudio()
    fun pauseAudio()
    fun stopAudio()
}