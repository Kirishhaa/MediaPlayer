package com.example.mediaplayer.data.datacontroller

import com.example.mediaplayer.data.models.Audio
import com.example.mediaplayer.data.AudioDecoder
import com.example.mediaplayer.data.models.AudioEntity

class FavoriteObjectOperations {

    fun findPosInAllList(position: Int, map: Map<Int, Audio>): Int {
        var counter = 0
        map.forEach {
            if (counter == position) return it.key
            counter++
        }
        return -1
    }

    fun findPosInFavoriteList(position: Int, map: Map<Int, Audio>): Int {
        var counter = 0
        map.forEach {
            if (it.key == position) {
                return counter
            }
            counter++
        }
        return -1
    }

    fun favoriteContainsPosition(position: Int, map: Map<Int, Audio>): Boolean {
        return map.contains(position)
    }

    fun favoriteContainsAudio(audio: Audio, map: Map<Int, Audio>): Boolean {
        return map.containsValue(audio)
    }

    fun addToFavoriteList(
        position: Int,
        audio: Audio,
        map: LinkedHashMap<Int, Audio>,
        decorator: List<AudioEntity>,
    ): MutableList<AudioEntity> {
        map[position] = audio
        val newValue = AudioDecoder.getAudioEntity(audio)
        val mutList = decorator.toMutableList()
        mutList.add(newValue)
        return mutList
    }

    fun removeFromFavoriteList(
        audio: Audio,
        map: LinkedHashMap<Int, Audio>,
        allAudioList: List<Audio>,
        decorator: List<AudioEntity>,
    ): MutableList<AudioEntity> {
        var positionMap = -1
        var positionList = -1
        val listDefault = map.values
        allAudioList.forEachIndexed { index, song ->
            if (song.path == audio.path) {
                positionMap = index
                return@forEachIndexed
            }
        }
        listDefault.forEachIndexed { index, song ->
            if (song.path == audio.path) {
                positionList = index
                return@forEachIndexed
            }
        }
        map.remove(positionMap)
        val decoratorList = decorator.toMutableList()
        decoratorList.removeAt(positionList)
        return decoratorList
    }
}