package com.example.mediaplayer.repository

import android.os.Environment
import androidx.core.net.toUri
import com.example.mediaplayer.models.Audio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class Repository {
    suspend fun loadData(): List<Audio> {
        val mutableAudioList = mutableListOf<Audio>()
        withContext(Dispatchers.IO) {
            val musicDirectory =
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).path)
            val f = File(musicDirectory.toString())
            val list = f.listFiles()
            list?.forEach {
                if (it.toUri().toString().contains("mp3")) {
                    mutableAudioList.add(Audio(it.absolutePath))
                }
            }
        }
        return mutableAudioList
    }
//
//    suspend fun refreshFavoriteAddress(favoriteList: List<Audio>): List<Audio> {
//        val mutableMusicList = mutableListOf<Audio>()
//        withContext(Dispatchers.IO) {
//            val mutableList = mutableListOf<String>()
//            for (i in favoriteList.indices) {
//                mutableList+=favoriteList[i].path.substring(
//                    favoriteList[i].path.lastIndexOf('/'),
//                    favoriteList.size - 1
//                )
//            }
//
//            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//                var currentFile = File(Environment.getExternalStorageDirectory().absolutePath)
//                val stack = Stack<File>()
//
//                stack.push(currentFile)
//                while (stack.isNotEmpty()) {
//                    currentFile = stack.pop()
//                    if(currentFile.isDirectory && currentFile.canRead()){
//                        currentFile.listFiles()?.forEach {
//                            stack.push(it)
//                        }
//                    } else if(currentFile.toUri().toString().contains("mp3")){
//                        if(mutableList.contains(currentFile.name)) {
//                            mutableMusicList += Audio(currentFile.absolutePath)
//                        }
//                    }
//                }
//            } else {
//                return@withContext
//            }
//        }
//        println(mutableMusicList)
//        return mutableMusicList
//    }
}