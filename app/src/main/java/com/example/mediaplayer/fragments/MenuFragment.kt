package com.example.mediaplayer.fragments

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mediaplayer.R
import com.example.mediaplayer.data.*
import com.example.mediaplayer.data.models.Audio
import com.example.mediaplayer.data.models.AudioEntity
import com.example.mediaplayer.data.models.MetaData
import com.example.mediaplayer.fragments.basefragments.BaseFragment
import com.example.mediaplayer.fragments.listfragments.HorizontalFragment
import com.example.mediaplayer.interfaces.markers.BaseListInteraction
import com.example.mediaplayer.interfaces.markers.SourceFragment
import com.example.mediaplayer.interfaces.navigation.FragmentNavigator

class MenuFragment : BaseFragment(R.layout.fragment_menu), SourceFragment {
    private val viewModel: AudioViewModel by lazy { ViewModelProvider(this)[AudioViewModel::class.java] }
    private lateinit var storage: Storage
    private lateinit var navigator: FragmentNavigator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storage = Storage(requireContext().applicationContext)
        navigator = FragmentNavigatorImpl(childFragmentManager, storage)
        viewModel.initialize(storage.readAllAudioList(),storage.readFavoriteMap())

        if (savedInstanceState == null) {
            navigator.navigate(HorizontalFragment())
        }

        viewModel.metaData.observe(viewLifecycleOwner) {
            childFragmentManager.fragments.forEach { fragment ->
                (fragment as BaseListInteraction).setMetaData(getMetaData(fragment.getIsFavoriteState()))
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
        navigator.navigate(fragment)
    }

    override fun getMetaData(isFavoriteFragment: Boolean): MetaData {
        return viewModel.getMetaData(isFavoriteFragment)
    }

    override fun getFavoriteList(): List<Audio> {
        return viewModel.hashMapFavorite.value?.values?.toList() ?: emptyList()
    }

    override fun getFavoriteMap(): Map<Int, Audio> {
        return viewModel.hashMapFavorite.value ?: emptyMap()
    }

    override fun favoriteContainsPosition(position: Int): Boolean {
        return viewModel.favoriteContainsPosition(position)
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
        viewModel.addToFavoriteList(position, audio)
    }

    override fun removeFromFavorite(audio: Audio) {
        viewModel.removeFromFavoriteList(audio)
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

    override fun favoriteContainsAudio(audio: Audio): Boolean {
        return viewModel.favoriteContainsAudio(audio)
    }
}