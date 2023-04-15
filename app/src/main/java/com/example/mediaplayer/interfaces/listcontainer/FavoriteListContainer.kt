package com.example.mediaplayer.interfaces.listcontainer

import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.AudioEntity

interface FavoriteListContainer {
    fun getFavoriteList(): List<Audio>
    fun getFavoriteDecorator(): List<AudioEntity>
}