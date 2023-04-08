package com.example.mediaplayer.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.Repository
import com.example.mediaplayer.data.SongMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AudioViewModel : ViewModel() {
    private val repo: Repository = Repository()
    private var mutableAllListMetaData = MutableLiveData<SongMetadata>()
    val allListMetaData: LiveData<SongMetadata> = mutableAllListMetaData
    private var mutableAudioList = MutableLiveData<List<Audio>>()
    val allAudioList: LiveData<List<Audio>> = mutableAudioList
    private var mutableHashMapFavorite = MutableLiveData<HashMap<Int, Audio>>()
    val hashMapFavorite : LiveData<HashMap<Int, Audio>> = mutableHashMapFavorite
    var isFavorite: Boolean = false
    private set

    init {
        if(mutableAllListMetaData.value==null)
            loadData()
    }

    fun updateAllListMetaData(metaData: SongMetadata) {
        mutableAllListMetaData.value = metaData
    }

    fun addToFavoriteSet(audio: Audio, position: Int) {
        if(mutableHashMapFavorite.value == null) {
            mutableHashMapFavorite.value = HashMap()
        }
        mutableHashMapFavorite.value?.put(position, audio)
    }

    fun removeFromFavoriteSet(audio: Audio) {
        val list = allAudioList.value
        var position = -1
        list?.forEachIndexed{ index, song ->
            if(song == audio) {
                position= index
                return@forEachIndexed
            }
        }
        mutableHashMapFavorite.value?.remove(position)
    }

    fun favoriteContains(position: Int): Boolean {
        return mutableHashMapFavorite.value?.contains(position) == true
    }

    fun favoriteListIsNotEmpty() : Boolean{
        return mutableHashMapFavorite.value?.isNotEmpty() == false
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            mutableAllListMetaData.postValue(SongMetadata())
            mutableAudioList.postValue(repo.loadData())
        }
    }
}