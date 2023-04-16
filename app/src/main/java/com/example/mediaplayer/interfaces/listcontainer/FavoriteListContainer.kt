package com.example.mediaplayer.interfaces.listcontainer

import com.example.mediaplayer.data.models.Audio
import com.example.mediaplayer.data.models.AudioEntity

interface FavoriteListContainer {
    fun getFavoriteList(): List<Audio>
    fun getFavoriteDecorator(): List<AudioEntity>
}