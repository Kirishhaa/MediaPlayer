package com.example.mediaplayer.data

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import com.example.mediaplayer.R

data class Audio(
    var imageArt: Bitmap? = null,
    val title: String,
    val duration: Long,
    val path: String,
    var imagePlayRes: Int = R.drawable.ic_play_arrow,
)