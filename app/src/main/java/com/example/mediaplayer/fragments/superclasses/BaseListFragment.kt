package com.example.mediaplayer.fragments.superclasses

import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.AudioEntity
import com.example.mediaplayer.data.SongMetadata
import com.example.mediaplayer.fragments.CustomAdapterAudio
import com.example.mediaplayer.fragments.MenuFragment
import com.example.mediaplayer.interfaces.*

abstract class BaseListFragment(resLayout: Int) : BaseFragment(resLayout),
    AdapterListener, AllListContainer, FragmentNavigator, FragmentBackPressed{
    private var audioController: AudioController? = null
    private var menuFragment: MenuFragment? = null
    protected var songMetaData: SongMetadata = SongMetadata()
    protected var audioList: List<Audio> = emptyList()
    protected var decoratorList: List<AudioEntity> = emptyList()
    protected var adapter: CustomAdapterAudio? = null
    open var isFavorite = false
    protected set

    override fun onAttach(context: Context) {
        super.onAttach(context)
        audioController = context as AudioController
        menuFragment = parentFragment as MenuFragment
    }

    override fun onDetach() {
        super.onDetach()
        menuFragment = null
        audioController = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isFavorite = savedInstanceState?.getBoolean("isFavorite") ?: isFavorite

        val meta = menuFragment?.getListMetaData()
        if(meta!=null) {
            if((isFavorite && meta.isFavorite) || (!isFavorite && !meta.isFavorite)) {
                songMetaData = meta
            }
        } else {
            songMetaData = SongMetadata()
        }

        audioList = if(isFavorite) {
            menuFragment?.getFavoriteList()?.toList() ?: emptyList()
        } else {
            menuFragment?.getAudioList() ?: emptyList()
        }

        decoratorList = if(isFavorite) {
            menuFragment?.getFavoriteDecorator() ?: emptyList()
        } else {
            menuFragment?.getAllListDecorator() ?: emptyList()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isFavorite", isFavorite)
    }

    override fun onPlayClicked(songMetadata: SongMetadata, audioList: List<Audio>, favoriteMap: Map<Int, Audio>?) {
        audioController?.playAudio(songMetadata, audioList, isFavorite, favoriteMap)
    }

    override fun onPauseClicked(songMetadata: SongMetadata) {
        audioController?.pauseAudio()
    }

    override fun onResumeClicked(songMetadata: SongMetadata) {
        audioController?.resumeAudio()
    }

    override fun onStopClicked() {
        audioController?.stopAudio()
    }

    override fun callbackMetadata(songMetadata: SongMetadata) {
        menuFragment?.updateAllListMetaData(songMetadata)
    }

    override fun setAllListMetaData(songMetadata: SongMetadata) {
        adapter?.setSongMetadata(songMetadata)
    }

    fun getFavoriteMap() : Map<Int, Audio>? {
        return menuFragment?.getFavoriteMap()
    }

    override fun setAllList(audioList: List<Audio>, decoratorList: List<AudioEntity>) {
        adapter?.setAudioList(audioList, decoratorList)
    }

    override fun navigate(fragment: BaseListFragment) {
        menuFragment?.navigate(fragment)
    }

    fun getFavoriteList(): List<Audio>? {
        return menuFragment?.getFavoriteList()
    }

    fun addFavoriteAudio(audio: Audio, position: Int) {
        menuFragment?.addToFavoriteList(audio, position)
    }

    fun removeFavoriteAudio(audio: Audio) {
        menuFragment?.removeFromFavoriteList(audio)
    }

    fun favoriteContains(position: Int): Boolean {
        return menuFragment?.favoriteContains(position) == true
    }

    fun favoriteListIsNotEmpty() : Boolean {
        return menuFragment?.favoriteListIsNotEmpty() == true
    }
}