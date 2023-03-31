package com.example.mediaplayer.fragments.superclasses

import android.content.Context
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.SongMetadata
import com.example.mediaplayer.fragments.CustomAdapterAudio
import com.example.mediaplayer.fragments.MenuFragment
import com.example.mediaplayer.interfaces.AdapterListener
import com.example.mediaplayer.interfaces.AudioController
import com.example.mediaplayer.interfaces.ListContainer

abstract class BaseListFragment(resLayout: Int): BaseFragment(resLayout),
    AdapterListener, ListContainer {
    private var audioController: AudioController? = null
    private var callback: MenuFragment? = null
    protected var audioMetaData: SongMetadata? = null
    protected var audioList: List<Audio> = emptyList()
    protected lateinit var adapter: CustomAdapterAudio

    override fun onAttach(context: Context) {
        super.onAttach(context)
        audioController = context as AudioController
        callback = parentFragment as MenuFragment
        audioMetaData = callback?.getAudioMetaData()
        audioList = callback?.getAudioList() ?: emptyList()
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
        audioController=null
    }

    override fun onPlayClicked(songMetadata: SongMetadata){
        audioController?.playAudio(songMetadata)
    }

    override fun onPauseClicked(songMetadata: SongMetadata){
        audioController?.pauseAudio()
        callbackMetadata(songMetadata)
    }

    override fun onResumeClicked(songMetadata: SongMetadata){
        audioController?.resumeAudio()
        callbackMetadata(songMetadata)
    }

    override fun callbackMetadata(songMetadata: SongMetadata) {
        callback?.updateMetaData(songMetadata)
    }

    override fun setSongsMetadata(songMetadata: SongMetadata){
        adapter.setSongMetadata(songMetadata)
    }

    override fun setList(audioList: List<Audio>) {
        adapter.setAudioList(audioList)
    }
}