package com.example.mediaplayer.interfaces

import com.example.mediaplayer.data.Audio

interface AdapterListener: Callback {
    fun onPlayClicked(position: Int, list: List<Audio>)
    fun onPauseClicked()
    fun onResumeClicked()
}