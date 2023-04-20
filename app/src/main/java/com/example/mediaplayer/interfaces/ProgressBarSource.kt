package com.example.mediaplayer.interfaces

interface ProgressBarSource {
    fun updateAudioCurrentTime(data: Pair<Int, Int>)
    fun getAudioCurrentTime(): Pair<Int, Int>
}