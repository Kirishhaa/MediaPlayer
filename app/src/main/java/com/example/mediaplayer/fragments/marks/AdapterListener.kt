package com.example.mediaplayer.fragments.marks

import com.example.mediaplayer.data.Audio

interface AdapterListener: PositionCallbacker {
    fun onPlayClicked(position: Int, list: List<Audio>)
    fun onPauseClicked()
    fun onResumeClicked()
}