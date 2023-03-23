package com.example.mediaplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.Repository

class AudioViewModel: ViewModel() {
    private val repo = Repository()
    private val mutableAudioList = MutableLiveData<ArrayList<Audio>>()
    val audioList: LiveData<ArrayList<Audio>> = mutableAudioList

    init {
        updateData()
    }

    private fun updateData(){
        mutableAudioList.value = repo.loadData()
    }
}