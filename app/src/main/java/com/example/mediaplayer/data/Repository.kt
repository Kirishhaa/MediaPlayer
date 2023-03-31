package com.example.mediaplayer.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class Repository {
    suspend fun loadData(): List<Audio> {
        val mutableAudioList = mutableListOf<Audio>()
        withContext(Dispatchers.IO) {
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
                            .toLong()
                    mutableAudioList.add(
                        Audio(
                            image,
                            it.nameWithoutExtension,
                            duration,
                            it.absolutePath
                        )
                    )
                }
            }
            mediaDataRetriever.release()
        }
        return mutableAudioList
    }
}