package com.example.mediaplayer.data

data class SongMetadata(
    val currentPosition: Int = -1,
    val state: PlaybackStatus = PlaybackStatus.PAUSED,
    val isFavorite: Boolean = false
) {
    constructor(songMetadata: SongMetadata) : this(
        songMetadata.currentPosition,
        songMetadata.state,
        songMetadata.isFavorite
    )
}