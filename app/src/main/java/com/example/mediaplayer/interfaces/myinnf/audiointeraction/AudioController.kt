package com.example.mediaplayer.interfaces.myinnf.audiointeraction

import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.MetaData

interface AudioController {
    fun playAudio(metadata: MetaData, audioList: List<Audio>, isFavorite: Boolean, favoriteMap: Map<Int, Audio>?=null)
    fun resumeAudio()
    fun pauseAudio()
    fun stopAudio()
}