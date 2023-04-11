package com.example.mediaplayer.interfaces

import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.AudioEntity

interface AllListContainer: AllListMetaDataContainer {
    fun setAllList(audioList: List<Audio>, decoratorList: List<AudioEntity>)
}