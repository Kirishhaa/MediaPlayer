package com.example.mediaplayer.fragments.marks

import com.example.mediaplayer.data.Audio

interface ListContainer {
    fun setList(audioList: List<Audio>)
}