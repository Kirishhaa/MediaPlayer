package com.example.mediaplayer.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import java.io.File

class Repository{

    fun loadData(): ArrayList<Audio> {
        val musicList = ArrayList<Audio>()
        val musicDirectory =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).path)
        val f = File(musicDirectory.toString())
        val list = f.listFiles()
        var image: Bitmap?
        val mediaDataRetriever = MediaMetadataRetriever()
        list?.forEach {
            if (MimeTypeMap.getFileExtensionFromUrl(it.toUri().toString()) == "mp3") {
                val src = it.absolutePath
                mediaDataRetriever.setDataSource(src)
                val byteArray = mediaDataRetriever.embeddedPicture
                image = if (byteArray != null) {
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                } else null
                val duration =
                    mediaDataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!
                        .toLong() / 1000
                musicList.add(Audio(image, it.nameWithoutExtension, duration, it.absolutePath))
            }
        }
        mediaDataRetriever.release()
        return musicList
    }
}