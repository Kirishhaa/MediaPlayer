package com.example.mediaplayer.fragments

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mediaplayer.R
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.AudioEntity
import com.example.mediaplayer.data.MetaData
import com.example.mediaplayer.data.Storage
import com.example.mediaplayer.fragments.superclasses.BaseFragment
import com.example.mediaplayer.interfaces.myinnf.markers.BaseListInteraction
import com.example.mediaplayer.interfaces.myinnf.markers.SourceFragment

class MenuFragment : BaseFragment(R.layout.fragment_menu), SourceFragment {
    private val viewModel: AudioViewModel by lazy { ViewModelProvider(this)[AudioViewModel::class.java] }
    private lateinit var storage: Storage

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storage = Storage(requireContext().applicationContext)

        val allAudioList = storage.readAllAudioList()
        val favoriteMap = storage.readFavoriteMap()

        viewModel.initialize(allAudioList, favoriteMap)

        if (savedInstanceState == null) {
            if (favoriteMap.isNotEmpty()) {
                childFragmentManager.beginTransaction()
                    .replace(R.id.menu_fragment_favorite_audio, HorizontalFragment.onInstance(true))
                    .commit()
            }
            childFragmentManager.beginTransaction()
                .replace(R.id.menu_fragment_all_audio, HorizontalFragment())
                .commit()
        }

        viewModel.metaData.observe(viewLifecycleOwner) { songMetadata ->
            childFragmentManager.fragments.forEach { fragment ->
                val curFragment = fragment as BaseListInteraction
                if ((curFragment.getIsFavoriteState() && songMetadata.isFavorite) ||
                    (!curFragment.getIsFavoriteState() && !songMetadata.isFavorite)
                ) {
                    fragment.setMetaData(songMetadata)
                } else {
                    fragment.setMetaData(MetaData())
                }
            }
        }

        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        viewModel.allAudioList.observe(viewLifecycleOwner) { audioList ->
            progressBar.isVisible = false
            childFragmentManager.fragments.forEach { fragment ->
                if (fragment is BaseListInteraction && !fragment.getIsFavoriteState())
                    fragment.setList(audioList, getAllDecorator())
            }
            storage.writeAllAudioList(audioList)
        }

        viewModel.hashMapFavorite.observe(viewLifecycleOwner) {
            childFragmentManager.fragments.forEach { fragment ->
                if (fragment is BaseListInteraction && fragment.getIsFavoriteState())
                    fragment.setList(getFavoriteList(), getFavoriteDecorator())
            }
        }
    }

    override fun navigate(fragment: Fragment) {
        if (fragment is HorizontalFragment) {
            if (favoriteIsNotEmpty())
                childFragmentManager.beginTransaction()
                    .replace(R.id.menu_fragment_favorite_audio, HorizontalFragment.onInstance(true))
                    .commit()
        } else {
            childFragmentManager.fragments.forEach {
                if (it is HorizontalFragment) {
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

    override fun getMetaData(): MetaData {
        return viewModel.metaData.value ?: MetaData()
    }

    override fun getFavoriteList(): List<Audio> {
        return viewModel.hashMapFavorite.value?.values?.toList() ?: emptyList()
    }

    override fun getFavoriteMap(): Map<Int, Audio> {
        return viewModel.hashMapFavorite.value ?: emptyMap()
    }

    override fun favoriteContains(position: Int): Boolean {
        return viewModel.favoriteContains(position)
    }

    override fun getAllList(): List<Audio> {
        return viewModel.allAudioList.value ?: emptyList()
    }

    override fun getAllDecorator(): List<AudioEntity> {
        return viewModel.allListDecorator.value ?: emptyList()
    }

    override fun getFavoriteDecorator(): List<AudioEntity> {
        return viewModel.favoriteDecorator.value ?: emptyList()
    }

    override fun addToFavorite(position: Int, audio: Audio) {
        viewModel.addToFavoriteSet(position, audio)
    }

    override fun removeFromFavorite(audio: Audio) {
        viewModel.removeFromFavoriteSet(audio)
    }

    override fun updateMetaData(metadata: MetaData) {
        viewModel.updateAllListMetaData(MetaData(metadata))
    }

    override fun favoriteIsNotEmpty(): Boolean {
        return viewModel.hashMapFavorite.value?.isNotEmpty() == true
    }

    override fun getIsFavoriteState(): Boolean {
        return viewModel.getFavoriteState()
    }
}