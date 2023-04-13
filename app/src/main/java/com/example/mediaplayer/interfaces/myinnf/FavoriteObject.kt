package com.example.mediaplayer.interfaces.myinnf

import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.AudioEntity

interface FavoriteObject {
    fun getFavoriteMap() : Map<Int, Audio>
    fun addToFavorite(position: Int, audio: Audio)
    fun removeFromFavorite(audio: Audio)
    fun favoriteContains(position: Int): Boolean
    fun favoriteIsNotEmpty(): Boolean
    fun getIsFavoriteState(): Boolean
}