package com.example.mediaplayer.fragments.playerfragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.dataoperations.AudioDecoder
import com.example.mediaplayer.dataoperations.datacontroller.FavoriteObjectOperations
import com.example.mediaplayer.dataoperations.datacontroller.MetaDataOperations
import com.example.mediaplayer.repository.Repository
import com.example.mediaplayer.models.Audio
import com.example.mediaplayer.models.AudioEntity
import com.example.mediaplayer.models.MetaData
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
    private val mutableAudioCurrentTime = MutableLiveData<Pair<Int, Int>>()
    val audioCurrentTime: LiveData<Pair<Int, Int>> = mutableAudioCurrentTime

    fun initialize(readAllAudioList: List<Audio>, readFavoriteMap: LinkedHashMap<Int, Audio>) {
        if (readAllAudioList.isEmpty()) {
            loadData()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    mutableAllListDecorator.postValue(readAllAudioList.map {
                        AudioDecoder.getAudioEntity(it)
                    })
                    mutableFavoriteDecorator.postValue(
                        readFavoriteMap.values.toList().map { AudioDecoder.getAudioEntity(it) }
                    )
                    mutableAudioList.postValue(readAllAudioList)
                    mutableHashMapFavorite.postValue(readFavoriteMap)
                    mutableAudioCurrentTime.postValue(Pair(-1, -1))
                } catch (e: Exception) {
                    loadData()
                }
            }
        }
    }

    fun updateAudioCurrentTime(data: Pair<Int, Int>) {
        mutableAudioCurrentTime.value = data
    }

    fun updateAllListMetaData(metaData: MetaData) {
        mutableMetaData.value = metaData
    }

    fun addToFavoriteList(position: Int, audio: Audio) {
        val foo = FavoriteObjectOperations()
        mutableFavoriteDecorator.value = foo.addToFavoriteList(
            position = position,
            audio = audio,
            map = mutableHashMapFavorite.value!!,
            decorator = mutableFavoriteDecorator.value!!
        )
    }

    fun removeFromFavoriteList(audio: Audio) {
        val foo = FavoriteObjectOperations()
        mutableFavoriteDecorator.value = foo.removeFromFavoriteList(
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
        val mdo = MetaDataOperations(FavoriteObjectOperations())
        return mdo.getMetaData(
            isFavoriteFragment = isFavoriteFragment,
            metaData = mutableMetaData.value,
            map = mutableHashMapFavorite.value
        )
    }

    fun favoriteContainsPosition(position: Int): Boolean {
        val foo = FavoriteObjectOperations()
        return foo.favoriteContainsPosition(position, mutableHashMapFavorite.value!!)
    }

    fun favoriteContainsAudio(audio: Audio): Boolean {
        val foo = FavoriteObjectOperations()
        return foo.favoriteContainsAudio(audio, mutableHashMapFavorite.value!!)
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            mutableMetaData.postValue(MetaData())
            val list = repo.loadData()
            mutableHashMapFavorite.postValue(LinkedHashMap(emptyMap<Int, Audio>()))
            mutableFavoriteDecorator.postValue(emptyList())
            mutableAllListDecorator.postValue(list.map { AudioDecoder.getAudioEntity(it) })
            mutableAudioList.postValue(list)
            mutableAudioCurrentTime.postValue(Pair(-1, -1))
        }
    }
}