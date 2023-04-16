package com.example.mediaplayer.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.data.*
import com.example.mediaplayer.data.models.Audio
import com.example.mediaplayer.data.models.AudioEntity
import com.example.mediaplayer.data.models.MetaData
import com.example.mediaplayer.data.datacontroller.DataController
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
    private val dataController = DataController()

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

    fun addToFavoriteList(position: Int, audio: Audio) {
        mutableFavoriteDecorator.value = dataController.foo.addToFavoriteList(
            position = position,
            audio = audio,
            map = mutableHashMapFavorite.value!!,
            decorator = mutableFavoriteDecorator.value!!
        )
    }

    fun removeFromFavoriteList(audio: Audio) {
        mutableFavoriteDecorator.value = dataController.foo.removeFromFavoriteList(
            audio = audio,
            map = mutableHashMapFavorite.value!!,
            allAudioList = allAudioList.value!!,
            decorator = favoriteDecorator.value!!
        )
    }

    fun getFavoriteState(): Boolean {
        return mutableMetaData.value?.isFavorite ?: false
    }

    fun getMetaData(isFavoriteFragment: Boolean): MetaData {
        return dataController.mdo.getMetaData(
            isFavoriteFragment = isFavoriteFragment,
            metaData = mutableMetaData.value,
            map = mutableHashMapFavorite.value
        )
    }

    fun favoriteContainsPosition(position: Int): Boolean {
        return dataController.foo.favoriteContainsPosition(position, mutableHashMapFavorite.value!!)
    }

    fun favoriteContainsAudio(audio: Audio): Boolean {
        return dataController.foo.favoriteContainsAudio(audio, mutableHashMapFavorite.value!!)
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