package com.example.mediaplayer.data.datacontroller

import android.os.Bundle
import com.example.mediaplayer.data.Storage
import com.example.mediaplayer.data.models.Audio
import com.example.mediaplayer.data.models.AudioEntity
import com.example.mediaplayer.data.models.MetaData
import com.example.mediaplayer.interfaces.markers.SourceFragment

class DataSetOperations {

    fun setStartedIsFavorite(isFavorite: Boolean, savedInstanceState: Bundle?): Boolean {
        return savedInstanceState?.getBoolean("isFavorite") ?: isFavorite
    }

    fun setStartedMetaData(sourceFragment: SourceFragment?, isFavorite: Boolean): MetaData {
        return sourceFragment?.getMetaData(isFavorite) ?: MetaData()
    }

    fun setStartedAudioList(sourceFragment: SourceFragment?, isFavorite: Boolean): List<Audio> {
        return if (isFavorite) {
            sourceFragment?.getFavoriteList() ?: emptyList()
        } else {
            sourceFragment?.getAllList() ?: emptyList()
        }
    }

    fun setStartedAudioList(storage: Storage, isFavorite: Boolean): List<Audio> {
        return if (isFavorite) {
            storage.readFavoriteMap().values.toList()
        } else {
            storage.readAllAudioList()
        }
    }

    fun setStartedCurrentAudio(currentIndex: Int, audioList: List<Audio>): Audio {
         if (currentIndex == -1 || currentIndex >= audioList.size) {
            throw IllegalArgumentException("cuurent index == -1 or < than audioList.size")
        } else {
            return audioList[currentIndex]
        }
    }

    fun setNextIndexInRangeAudioList(currentIndex: Int, audioList: List<Audio>) : Int {
        return if (currentIndex == audioList.size - 1) {
            0
        } else currentIndex+1
    }

    fun setPreviousIndexInRangeAudioList(currentIndex: Int, audioList: List<Audio>) : Int {
        return if (currentIndex == 0) {
            audioList.size - 1
        } else currentIndex-1
    }

    fun setStartedDecoratorList(
        sourceFragment: SourceFragment?,
        isFavorite: Boolean,
    ): List<AudioEntity> {
        return if (isFavorite) {
            sourceFragment?.getFavoriteDecorator() ?: emptyList()
        } else {
            sourceFragment?.getAllDecorator() ?: emptyList()
        }
    }
}