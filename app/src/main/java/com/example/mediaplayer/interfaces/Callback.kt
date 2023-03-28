package com.example.mediaplayer.interfaces

import com.example.mediaplayer.data.PlaybackStatus

interface Callback {
    fun callbackItem(position: Int, state: PlaybackStatus)
}