package com.example.mediaplayer.interfaces.listcontainer

import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.AudioEntity

interface AllListContainer {
    fun getAllList(): List<Audio>
    fun getAllDecorator(): List<AudioEntity>
}