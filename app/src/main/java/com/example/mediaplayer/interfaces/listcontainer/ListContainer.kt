package com.example.mediaplayer.interfaces.listcontainer

import com.example.mediaplayer.data.models.Audio
import com.example.mediaplayer.data.models.AudioEntity

interface ListContainer {
    fun setList(songsList: List<Audio>, decoratorList: List<AudioEntity>)
}