package com.example.mediaplayer.interfaces.listcontainer

import com.example.mediaplayer.models.Audio
import com.example.mediaplayer.models.AudioEntity

interface FavoriteListContainer {
    fun getFavoriteList(): List<Audio>
    fun getFavoriteDecorator(): List<AudioEntity>
}