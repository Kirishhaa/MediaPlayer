package com.example.mediaplayer.data

import android.graphics.Bitmap
import com.example.mediaplayer.R

data class Audio(
    var imageArt: Bitmap? = null,
    val title: String,
    val duration: Long,
    val path: String,
    var imagePlayRes: Int = R.drawable.ic_play_arrow,
) : java.io.Serializable