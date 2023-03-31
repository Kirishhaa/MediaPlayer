package com.example.mediaplayer.interfaces

import com.example.mediaplayer.data.SongMetadata

interface AdapterListener : Callback {
    fun onPlayClicked(songMetadata: SongMetadata)
    fun onPauseClicked(songMetadata: SongMetadata)
    fun onResumeClicked(songMetadata: SongMetadata)
}