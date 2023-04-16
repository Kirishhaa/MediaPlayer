package com.example.mediaplayer.models

data class MetaData(
    val currentPosition: Int = -1,
    val state: PlaybackStatus = PlaybackStatus.PAUSED,
    val isFavorite: Boolean = false,
) {
    constructor(metadata: MetaData) : this(
        metadata.currentPosition,
        metadata.state,
        metadata.isFavorite
    )
}