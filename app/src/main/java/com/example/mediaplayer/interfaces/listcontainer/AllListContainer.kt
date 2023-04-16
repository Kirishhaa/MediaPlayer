package com.example.mediaplayer.interfaces.listcontainer

import com.example.mediaplayer.data.models.Audio
import com.example.mediaplayer.data.models.AudioEntity

interface AllListContainer {
    fun getAllList(): List<Audio>
    fun getAllDecorator(): List<AudioEntity>
}