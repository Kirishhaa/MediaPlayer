package com.example.mediaplayer.data

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

data class Audio(val image: Bitmap?=null, val title: String, val duration: Long, val path: String): java.io.Serializable