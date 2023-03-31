package com.example.mediaplayer.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.PlaybackStatus
import com.example.mediaplayer.data.Repository
import com.example.mediaplayer.data.SongMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AudioViewModel : ViewModel() {
    private val repo: Repository = Repository()
    private var mutableMetaData = MutableLiveData<SongMetadata>()
    val metaData: LiveData<SongMetadata> = mutableMetaData
    private var mutableAudioList = MutableLiveData<List<Audio>>()
    val audioList: LiveData<List<Audio>> = mutableAudioList

    init {
        if (mutableMetaData.value == null) {
            loadData()
        }
    }

    fun updateSongMetadata(metaData: SongMetadata) {
        mutableMetaData.value = metaData
    }

    fun updateAudioList(audioList: List<Audio>) {
        mutableAudioList.value = audioList
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            mutableMetaData.postValue(SongMetadata())
            mutableAudioList.postValue(repo.loadData())
        }
    }
}