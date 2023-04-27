package com.example.mediaplayer.interfaces.progressbar

interface ProgressBarSource {
    fun updateAudioCurrentTime(data: Pair<Int, Int>)
    fun getAudioCurrentTime(): Pair<Int, Int>
}