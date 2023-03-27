package com.example.mediaplayer.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class Repository{
    fun loadData(): LiveData<List<Audio>> {
        val liveData = MutableLiveData<List<Audio>>()
        CoroutineScope(Dispatchers.IO).launch {
            val mutableAudioList = mutableListOf<Audio>()
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
                    mutableAudioList.add(
                        Audio(
                            image,
                            it.nameWithoutExtension,
                            duration,
                            it.absolutePath
                        )
                    )
                    liveData.postValue(mutableAudioList)
                }
            }
            mediaDataRetriever.release()
        }
        return liveData
    }
}