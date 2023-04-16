package com.example.mediaplayer.repository

import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.example.mediaplayer.models.Audio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class Repository {
    suspend fun loadData(): List<Audio> {
        val mutableAudioList = mutableListOf<Audio>()
        withContext(Dispatchers.IO) {
            Log.d("REPOSITORY", "LOADING DATA")
            val musicDirectory =
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).path)
            val f = File(musicDirectory.toString())
            val list = f.listFiles()
            list?.forEach {
                Log.i("REPOSITORY LIST", MimeTypeMap.getFileExtensionFromUrl(it.toUri().toString()))
                if (it.toUri().toString().contains("mp3")) {
                    mutableAudioList.add(Audio(it.absolutePath))
                }
            }
        }
        return mutableAudioList
    }
}