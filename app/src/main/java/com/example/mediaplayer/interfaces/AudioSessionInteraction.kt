package com.example.mediaplayer.interfaces

import com.example.mediaplayer.data.SongMetadata

interface AudioSessionInteraction {
    fun getCallback(songMetadata: SongMetadata)
}