package com.example.mediaplayer.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.Repository

class AudioViewModel: ViewModel() {
    private val repo = Repository()
    private var mutableAudioList: MutableLiveData<List<Audio>> = MutableLiveData()
    val audioList: LiveData<List<Audio>> = mutableAudioList

    init {
        if(mutableAudioList.value == null) loadData()
    }

    private fun loadData(){
        val a = repo.loadData()
        a.observeForever {
            mutableAudioList.postValue(it)
        }
    }
}