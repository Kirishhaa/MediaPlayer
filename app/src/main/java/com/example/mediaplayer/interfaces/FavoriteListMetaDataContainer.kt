package com.example.mediaplayer.interfaces

import com.example.mediaplayer.data.SongMetadata

interface FavoriteListMetaDataContainer {
    fun setFavoriteMetaData(songMetadata: SongMetadata)
}