package com.example.mediaplayer.interfaces

import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.SongMetadata

interface AdapterListener : Callback {
    fun onPlayClicked(songMetadata: SongMetadata, audioList: List<Audio>)
    fun onPauseClicked(songMetadata: SongMetadata)
    fun onResumeClicked(songMetadata: SongMetadata)
    fun onStopClicked()
}