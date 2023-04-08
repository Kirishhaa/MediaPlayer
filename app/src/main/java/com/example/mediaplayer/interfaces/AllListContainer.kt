package com.example.mediaplayer.interfaces

import com.example.mediaplayer.data.Audio

interface AllListContainer: AllListMetaDataContainer {
    fun setAllList(audioList: List<Audio>)
}