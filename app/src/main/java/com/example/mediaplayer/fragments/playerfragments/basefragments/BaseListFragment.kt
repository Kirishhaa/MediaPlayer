package com.example.mediaplayer.fragments.playerfragments.basefragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.mediaplayer.dataoperations.datacontroller.DataSetOperations
import com.example.mediaplayer.models.Audio
import com.example.mediaplayer.models.AudioEntity
import com.example.mediaplayer.models.MetaData
import com.example.mediaplayer.fragments.playerfragments.CustomAdapterAudio
import com.example.mediaplayer.interfaces.audiointeraction.AudioController
import com.example.mediaplayer.interfaces.markers.BaseListInteraction
import com.example.mediaplayer.interfaces.markers.SourceFragment

abstract class BaseListFragment(resLayout: Int) : BaseDataFragment(resLayout), BaseListInteraction {
    private var audioController: AudioController? = null
    private var sourceFragment: SourceFragment? = null

    open var toolbarTitle: String = "Menu"
    open var toolbarShowShuffleBox: Boolean = false

    protected var adapter: CustomAdapterAudio? = null

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

        sourceFragment?.setTitle(toolbarTitle)
        sourceFragment?.showShuffleBox(toolbarShowShuffleBox)

        DataSetOperations().run {
            isFavorite = setStartedIsFavorite(isFavorite, savedInstanceState)
            metaData = setStartedMetaData(sourceFragment, isFavorite)
            decoratorList = setStartedDecoratorList(sourceFragment, isFavorite)
            audioList = setStartedAudioList(sourceFragment, isFavorite)
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

    override fun setMetaData(metadata: MetaData) {
        adapter?.setMetaData(metadata)
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

    override fun navigate(fragment: Fragment, isFavorite: Boolean) {
        sourceFragment?.navigate(fragment, this.isFavorite)
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