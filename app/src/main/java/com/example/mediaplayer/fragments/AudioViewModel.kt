package com.example.mediaplayer.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AudioViewModel: ViewModel() {
    private val repo = Repository()
    private var mutableAudioList: MutableLiveData<List<Audio>> = MutableLiveData()
    val audioList: LiveData<List<Audio>> = mutableAudioList

    init {
        if(mutableAudioList.value == null) loadData()
    }

    private fun loadData(){
        viewModelScope.launch(Dispatchers.IO) {
            repo.loadData()
        }
        mutableAudioList.value = repo.audioList
    }
}