package com.example.mediaplayer.interfaces.audiointeraction

import com.example.mediaplayer.models.Audio
import com.example.mediaplayer.models.MetaData

interface AudioController {
    fun playAudio(
        metadata: MetaData,
        audioList: List<Audio>,
        isFavorite: Boolean,
        favoriteMap: Map<Int, Audio>? = null,
    )

    fun resumeAudio()
    fun pauseAudio()
    fun stopAudio()
}