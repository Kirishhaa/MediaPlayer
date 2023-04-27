package com.example.mediaplayer.fragments.playerfragments

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mediaplayer.R
import com.example.mediaplayer.storageutils.Storage
import com.example.mediaplayer.models.Audio
import com.example.mediaplayer.models.AudioEntity
import com.example.mediaplayer.models.MetaData
import com.example.mediaplayer.fragments.BaseFragment
import com.example.mediaplayer.interfaces.progressbar.ProgressBarContainer
import com.example.mediaplayer.fragments.playerfragments.listfragments.HorizontalFragment
import com.example.mediaplayer.interfaces.markers.BaseListInteraction
import com.example.mediaplayer.interfaces.markers.SourceFragment
import com.example.mediaplayer.interfaces.navigation.FragmentNavigator
import com.example.mediaplayer.toolbar.CustomToolBar
import java.lang.ref.WeakReference

class MenuFragment : BaseFragment(R.layout.fragment_menu), SourceFragment {
    private val viewModel: AudioViewModel by lazy { ViewModelProvider(this)[AudioViewModel::class.java] }
    private lateinit var navigator: FragmentNavigator
    private lateinit var toolBar: CustomToolBar

    private val favFr by lazy { view?.findViewById<FrameLayout>(R.id.menu_fragment_favorite_audio) }
    private val allFr by lazy { view?.findViewById<FrameLayout>(R.id.menu_fragment_all_audio) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolBar = view.findViewById(R.id.toolbar)

        val storage = Storage(requireContext().applicationContext)
        navigator = FragmentNavigatorImpl(
            WeakReference<FrameLayout>(allFr).get(),
            WeakReference<FrameLayout>(favFr).get(),
            childFragmentManager,
            storage
        )

        if (savedInstanceState == null) {
            viewModel.initialize(storage.readAllAudioList(), storage.readFavoriteMap())
            navigator.navigate(HorizontalFragment())
        } else {
            favFr?.isVisible = savedInstanceState.getBoolean("favoriteIsVisible")
            allFr?.isVisible = savedInstanceState.getBoolean("allIsVisible")
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

        viewModel.audioCurrentTime.observe(viewLifecycleOwner) {
            childFragmentManager.fragments.forEach { fragment ->
                if (fragment is ProgressBarContainer) {
                    fragment.setCurrentTime(it)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("favoriteIsVisible", favFr?.isVisible ?: false)
        outState.putBoolean("allIsVisible", allFr?.isVisible ?: false)
    }

    //PROGRESSBAR
    override fun updateAudioCurrentTime(data: Pair<Int, Int>) {
        viewModel.updateAudioCurrentTime(data)
    }

    override fun getAudioCurrentTime(): Pair<Int, Int> {
        return viewModel.audioCurrentTime.value ?: Pair(-1, -1)
    }


    //TOOLBAR
    override fun setTitle(title: String) {
        toolBar.setTitle(title)
    }

    override fun showShuffleBox(show: Boolean) {
        toolBar.showShuffleBox(show)
    }

    //NAVIGATOR
    override fun navigate(fragment: Fragment, isFavorite: Boolean) {
        navigator.navigate(fragment, isFavorite)
    }

    //METADATA
    override fun getMetaData(isFavoriteFragment: Boolean): MetaData {
        return viewModel.getMetaData(isFavoriteFragment)
    }

    override fun updateMetaData(metadata: MetaData) {
        viewModel.updateAllListMetaData(MetaData(metadata))
    }

    //LISTS
    override fun getFavoriteList(): List<Audio> {
        return viewModel.hashMapFavorite.value?.values?.toList() ?: emptyList()
    }

    override fun getAllList(): List<Audio> {
        return viewModel.allAudioList.value ?: emptyList()
    }

    //DECORATORS
    override fun getAllDecorator(): List<AudioEntity> {
        return viewModel.allListDecorator.value ?: emptyList()
    }

    override fun getFavoriteDecorator(): List<AudioEntity> {
        return viewModel.favoriteDecorator.value ?: emptyList()
    }

    //FAVORITE OBJECT
    override fun getFavoriteMap(): Map<Int, Audio> {
        return viewModel.hashMapFavorite.value ?: emptyMap()
    }

    override fun addToFavorite(position: Int, audio: Audio) {
        viewModel.addToFavoriteList(position, audio)
    }

    override fun removeFromFavorite(audio: Audio) {
        viewModel.removeFromFavoriteList(audio)
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

    override fun favoriteContainsPosition(position: Int): Boolean {
        return viewModel.favoriteContainsPosition(position)
    }
}