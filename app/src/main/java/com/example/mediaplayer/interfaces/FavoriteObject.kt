package com.example.mediaplayer.interfaces

import com.example.mediaplayer.data.Audio

interface FavoriteObject {
    fun getFavoriteMap() : Map<Int, Audio>
    fun addToFavorite(position: Int, audio: Audio)
    fun removeFromFavorite(audio: Audio)
    fun favoriteContainsPosition(position: Int): Boolean
    fun favoriteIsNotEmpty(): Boolean
    fun getIsFavoriteState(): Boolean
    fun favoriteContainsAudio(audio: Audio) : Boolean
}