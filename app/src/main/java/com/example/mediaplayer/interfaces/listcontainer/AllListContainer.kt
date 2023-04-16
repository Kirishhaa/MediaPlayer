package com.example.mediaplayer.interfaces.listcontainer

import com.example.mediaplayer.models.Audio
import com.example.mediaplayer.models.AudioEntity

interface AllListContainer {
    fun getAllList(): List<Audio>
    fun getAllDecorator(): List<AudioEntity>
}