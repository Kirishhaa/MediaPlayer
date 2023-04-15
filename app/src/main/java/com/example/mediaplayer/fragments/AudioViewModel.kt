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
    private val mutableMetaData = MutableLiveData<MetaData>()
    val metaData: LiveData<MetaData> = mutableMetaData
    private val mutableAudioList = MutableLiveData<List<Audio>>()
    val allAudioList: LiveData<List<Audio>> = mutableAudioList
    private val mutableHashMapFavorite = MutableLiveData<LinkedHashMap<Int, Audio>>()
    val hashMapFavorite: LiveData<LinkedHashMap<Int, Audio>> = mutableHashMapFavorite
    private val mutableAllListDecorator = MutableLiveData<List<AudioEntity>>()
    val allListDecorator: LiveData<List<AudioEntity>> = mutableAllListDecorator
    private val mutableFavoriteDecorator = MutableLiveData<List<AudioEntity>>()
    val favoriteDecorator: LiveData<List<AudioEntity>> = mutableFavoriteDecorator

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

    fun updateAllListMetaData(metaData: MetaData) {
        mutableMetaData.value = metaData
    }

    fun addToFavoriteSet(position: Int, audio: Audio) {
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

    fun getFavoriteState() : Boolean {
        return mutableMetaData.value?.isFavorite ?: false
    }

    fun getMetaData(isFavoriteFragment: Boolean): MetaData {
        val curMetaData = mutableMetaData.value ?: MetaData()
        if(isFavoriteFragment) {
            if(curMetaData.isFavorite) {
                return curMetaData
            } else {
                return MetaData(findPosInFavoriteList(curMetaData.currentPosition),
                    curMetaData.state,
                    curMetaData.isFavorite)
            }
        } else {
            if(!curMetaData.isFavorite) {
                return curMetaData
            } else {
                return MetaData(findPosInAllList(curMetaData.currentPosition),
                curMetaData.state,
                curMetaData.isFavorite)
            }
        }
    }

    fun favoriteContainsPosition(position: Int): Boolean {
        return mutableHashMapFavorite.value?.contains(position) == true
    }

    fun favoriteContainsAudio(audio: Audio): Boolean {
        return mutableHashMapFavorite.value?.containsValue(audio) == true
    }

    fun findPosInAllList(position: Int) : Int {
        var counter = 0
        mutableHashMapFavorite.value?.forEach {
            if(counter==position) return it.key
            counter++
        }
        return -1
    }

    fun findPosInFavoriteList(position: Int) : Int {
        var counter = 0
        mutableHashMapFavorite.value?.forEach {
            if(it.key==position) {
                return counter
            }
            counter++
        }
        return -1
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            mutableMetaData.postValue(MetaData())
            val list = repo.loadData()
            mutableAllListDecorator.postValue(list.map { AudioDecoder.getAudioEntity(it) })
            mutableAudioList.postValue(list)
        }
    }
}