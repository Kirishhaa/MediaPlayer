package com.example.mediaplayer.fragments.superclasses

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.AudioEntity
import com.example.mediaplayer.data.MetaData
import com.example.mediaplayer.fragments.CustomAdapterAudio
import com.example.mediaplayer.interfaces.*
import com.example.mediaplayer.interfaces.audiointeraction.AudioController
import com.example.mediaplayer.interfaces.markers.BaseListInteraction
import com.example.mediaplayer.interfaces.SourceFragment

abstract class BaseListFragment(resLayout: Int) : BaseFragment(resLayout), BaseListInteraction {
    private var audioController: AudioController? = null
    private var sourceFragment: SourceFragment? = null

    @Suppress("INAPPLICABLE_JVM_NAME")
    @set:JvmName("songMetaDataField")
    @get:JvmName("songMetaDataField")
    override var metaData: MetaData = MetaData()
    override var audioList: List<Audio> = emptyList()
    override var decoratorList: List<AudioEntity> = emptyList()
    protected var adapter: CustomAdapterAudio? = null
    override var isFavorite = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        audioController = context as AudioController
        sourceFragment = parentFragment as SourceFragment
    }

    override fun onDetach() {
        super.onDetach()
        sourceFragment = null
        audioController = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isFavorite = savedInstanceState?.getBoolean("isFavorite") ?: isFavorite

        metaData = sourceFragment?.getMetaData(isFavorite) ?: MetaData()

        audioList = if (isFavorite) {
            sourceFragment?.getFavoriteList()?.toList() ?: emptyList()
        } else {
            sourceFragment?.getAllList() ?: emptyList()
        }

        decoratorList = if (isFavorite) {
            sourceFragment?.getFavoriteDecorator() ?: emptyList()
        } else {
            sourceFragment?.getAllDecorator() ?: emptyList()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isFavorite", isFavorite)
    }

    override fun getIsFavoriteState() = isFavorite

    override fun sendPlayAudio(metadata: MetaData, audioList: List<Audio>) {
        audioController?.playAudio(metadata, audioList, isFavorite, getFavoriteMap())
    }

    override fun sendPauseAudio(metadata: MetaData) {
        audioController?.pauseAudio()
    }

    override fun sendResumeAudio(metadata: MetaData) {
        audioController?.resumeAudio()
    }

    override fun sendStopAudio() {
        audioController?.stopAudio()
    }

    override fun callbackMetaData(metadata: MetaData) {
        sourceFragment?.updateMetaData(metadata)
    }

    override fun setMetaData(metadata: MetaData) {
        adapter?.setSongMetadata(metadata)
    }

    override fun getFavoriteMap(): Map<Int, Audio> {
        return sourceFragment?.getFavoriteMap() ?: emptyMap()
    }

    override fun favoriteContainsAudio(audio: Audio): Boolean {
        return sourceFragment?.favoriteContainsAudio(audio) == true
    }

    override fun setList(songsList: List<Audio>, decoratorList: List<AudioEntity>) {
        this.audioList = songsList
        this.decoratorList = decoratorList
        adapter?.setAudioList(songsList, decoratorList)
    }

    override fun navigate(fragment: Fragment) {
        sourceFragment?.navigate(fragment)
    }

    override fun getFavoriteList(): List<Audio> {
        return sourceFragment?.getFavoriteList() ?: emptyList()
    }

    override fun addToFavorite(position: Int, audio: Audio) {
        sourceFragment?.addToFavorite(position, audio)
    }

    override fun removeFromFavorite(audio: Audio) {
        sourceFragment?.removeFromFavorite(audio)
    }

    override fun favoriteContainsPosition(position: Int): Boolean {
        return sourceFragment?.favoriteContainsPosition(position) == true
    }

    override fun favoriteIsNotEmpty(): Boolean {
        return sourceFragment?.favoriteIsNotEmpty() == true
    }

    override fun getFavoriteDecorator(): List<AudioEntity> {
        return sourceFragment?.getFavoriteDecorator() ?: emptyList()
    }
}