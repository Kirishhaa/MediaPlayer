package com.example.mediaplayer.interfaces.listcontainer

import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.AudioEntity

interface ListContainer {
    fun setList(songsList: List<Audio>, decoratorList: List<AudioEntity>)
}