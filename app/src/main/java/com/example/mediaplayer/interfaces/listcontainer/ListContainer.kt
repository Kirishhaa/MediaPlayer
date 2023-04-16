package com.example.mediaplayer.interfaces.listcontainer

import com.example.mediaplayer.models.Audio
import com.example.mediaplayer.models.AudioEntity

interface ListContainer {
    fun setList(songsList: List<Audio>, decoratorList: List<AudioEntity>)
}