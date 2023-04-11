package com.example.mediaplayer.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AudioViewModel : ViewModel() {
    private val repo: Repository = Repository()
    private val mutableAllListMetaData = MutableLiveData<SongMetadata>()
    val allListMetaData: LiveData<SongMetadata> = mutableAllListMetaData
    private val mutableAudioList = MutableLiveData<List<Audio>>()
    val allAudioList: LiveData<List<Audio>> = mutableAudioList
    private val mutableHashMapFavorite = MutableLiveData<LinkedHashMap<Int, Audio>>()
    val hashMapFavorite: LiveData<LinkedHashMap<Int, Audio>> = mutableHashMapFavorite
    private val mutableAllListDecorator = MutableLiveData<List<AudioEntity>>()
    val allListDecorator: LiveData<List<AudioEntity>> = mutableAllListDecorator
    private val mutableFavoriteDecorator = MutableLiveData<List<AudioEntity>>()
    val favoriteDecorator: LiveData<List<AudioEntity>> = mutableFavoriteDecorator
    var isFavorite: Boolean = false
        private set

    fun initialize(readAllAudioList: List<Audio>, readFavoriteMap: LinkedHashMap<Int, Audio>) {
        if (readAllAudioList.isEmpty()) {
            loadData()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                mutableAllListDecorator.postValue(readAllAudioList.map {
                    AudioDecoder.getAudioEntity(it)
                })
                mutableFavoriteDecorator.postValue(
                    readFavoriteMap.values.toList().map { AudioDecoder.getAudioEntity(it) }
                )
                mutableAudioList.postValue(readAllAudioList)
                mutableHashMapFavorite.postValue(readFavoriteMap)
            }
        }
    }

    fun updateAllListMetaData(metaData: SongMetadata) {
        mutableAllListMetaData.value = metaData
    }

    fun addToFavoriteSet(audio: Audio, position: Int) {
        mutableHashMapFavorite.value?.put(position, audio)
        val newValue = AudioDecoder.getAudioEntity(audio)
        val mutList = mutableFavoriteDecorator.value!!.toMutableList()
        mutList.add(newValue)
        mutableFavoriteDecorator.value = mutList
    }

    fun removeFromFavoriteSet(audio: Audio) {
        val listMap = allAudioList.value
        var positionMap = -1
        var positionList = -1
        val listDefault = mutableHashMapFavorite.value?.values
        listMap?.forEachIndexed { index, song ->
            if (song.path == audio.path) {
                positionMap = index
                return@forEachIndexed
            }
        }
        listDefault?.forEachIndexed{index, song ->
            if (song.path == audio.path) {
                positionList = index
                return@forEachIndexed
            }
        }
        mutableHashMapFavorite.value?.remove(positionMap)
        val decoratorList = mutableFavoriteDecorator.value!!.toMutableList()
        decoratorList.removeAt(positionList)
        mutableFavoriteDecorator.value = decoratorList
    }

    fun favoriteContains(position: Int): Boolean {
        return mutableHashMapFavorite.value?.contains(position) == true
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            mutableAllListMetaData.postValue(SongMetadata())
            val list = repo.loadData()
            mutableAllListDecorator.postValue(list.map { AudioDecoder.getAudioEntity(it) })
            mutableAudioList.postValue(list)
        }
    }
}