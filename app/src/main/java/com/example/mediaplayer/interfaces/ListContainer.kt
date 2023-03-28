package com.example.mediaplayer.interfaces

import com.example.mediaplayer.data.Audio

interface ListContainer {
    fun setList(audioList: List<Audio>)
}