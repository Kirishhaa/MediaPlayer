package com.example.mediaplayer.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.mediaplayer.R
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.AudioEntity
import com.example.mediaplayer.data.SongMetadata
import com.example.mediaplayer.data.Storage
import com.example.mediaplayer.fragments.superclasses.BaseFragment
import com.example.mediaplayer.fragments.superclasses.BaseListFragment

class MenuFragment : BaseFragment(R.layout.fragment_menu) {
    private val viewModel: AudioViewModel by lazy { ViewModelProvider(this)[AudioViewModel::class.java] }
    private lateinit var storage: Storage


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storage = Storage(requireContext().applicationContext)
        val allAudioList = storage.readAllAudioList()
        val favoriteMap = storage.readFavoriteMap()

        viewModel.initialize(allAudioList, favoriteMap)

        if (savedInstanceState == null) {
            if(favoriteMap.isNotEmpty()) {
                childFragmentManager.beginTransaction()
                    .replace(R.id.menu_fragment_favorite_audio, HorizontalFragment.onInstance(true))
                    .commit()
            }
            childFragmentManager.beginTransaction()
                .replace(R.id.menu_fragment_all_audio, HorizontalFragment())
                .commit()
        }

        viewModel.allListMetaData.observe(viewLifecycleOwner) { songMetadata ->
            childFragmentManager.fragments.forEach { fragment ->
                val curFragment = fragment as BaseListFragment
                if(curFragment.isFavorite && songMetadata.isFavorite) {
                    fragment.setAllListMetaData(songMetadata)
                } else if(!curFragment.isFavorite && !songMetadata.isFavorite){
                    fragment.setAllListMetaData(songMetadata)
                } else {
                    fragment.setAllListMetaData(SongMetadata())
                }
            }
        }

        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        viewModel.allAudioList.observe(viewLifecycleOwner) { audioList ->
            progressBar.isVisible = false
            childFragmentManager.fragments.forEach { fragment ->
                if (fragment is BaseListFragment && !fragment.isFavorite)
                    fragment.setAllList(audioList, getAllListDecorator()!!)
            }
            storage.writeAllAudioList(audioList)
        }

        viewModel.hashMapFavorite.observe(viewLifecycleOwner) {
            childFragmentManager.fragments.forEach { fragment ->
                if (fragment is BaseListFragment && fragment.isFavorite)
                    fragment.setAllList(getFavoriteList()!!, getFavoriteDecorator()!!)
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun navigate(fragment: BaseListFragment) {
        if(fragment is HorizontalFragment){
            if(favoriteListIsNotEmpty())
            childFragmentManager.beginTransaction()
                .replace(R.id.menu_fragment_favorite_audio, HorizontalFragment.onInstance(true))
                .commit()
        } else {
            childFragmentManager.fragments.forEach {
                if(it is HorizontalFragment) {
                    childFragmentManager.beginTransaction()
                        .remove(it)
                        .commit()
                    return@forEach
                }
            }
        }
            childFragmentManager.beginTransaction()
                .replace(R.id.menu_fragment_all_audio, fragment)
                .commit()
    }

    fun getListMetaData(): SongMetadata? {
        return viewModel.allListMetaData.value
    }

    fun getFavoriteList(): List<Audio>? {
        return viewModel.hashMapFavorite.value?.values?.toList()
    }

    fun getFavoriteMap() : Map<Int, Audio>? {
        return viewModel.hashMapFavorite.value
    }

    fun favoriteContains(position: Int): Boolean {
        return viewModel.favoriteContains(position)
    }

    fun getAudioList(): List<Audio>? {
        return viewModel.allAudioList.value
    }

    fun getAllListDecorator() : List<AudioEntity>? {
        return viewModel.allListDecorator.value
    }

    fun getFavoriteDecorator() : List<AudioEntity>? {
        return viewModel.favoriteDecorator.value
    }

    fun addToFavoriteList(audio: Audio, position: Int) {
        viewModel.addToFavoriteSet(audio, position)
    }

    fun removeFromFavoriteList(audio: Audio) {
        viewModel.removeFromFavoriteSet(audio)
    }

    fun updateAllListMetaData(songMetadata: SongMetadata) {
        viewModel.updateAllListMetaData(SongMetadata(songMetadata))
    }

    fun favoriteListIsNotEmpty() : Boolean {
        return  viewModel.hashMapFavorite.value?.isNotEmpty() == true
    }
}