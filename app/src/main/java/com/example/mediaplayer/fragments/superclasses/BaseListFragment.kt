package com.example.mediaplayer.fragments.superclasses

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.PlaybackStatus
import com.example.mediaplayer.interfaces.AdapterListener
import com.example.mediaplayer.interfaces.AudioController
import com.example.mediaplayer.interfaces.ListContainer
import com.example.mediaplayer.interfaces.Callback

abstract class BaseListFragment(private val resLayout: Int): BaseFragment(resLayout),
    AdapterListener, ListContainer {
    private var audioController: AudioController? = null
    private var callback: BaseFragment? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        audioController = context as AudioController
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        callback = parentFragment as BaseFragment
        this.audioList = callback!!.audioList
        if(currentPosition==-1) this.currentPosition = callback!!.currentPosition
        this.state = callback!!.state
        return inflater.inflate(resLayout, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callback = null
    }

    override fun onDetach() {
        super.onDetach()
        audioController=null
    }

    override fun onPlayClicked(position: Int, list: List<Audio>){
        audioController?.onPlayAudio(position,list)
    }

    override fun onPauseClicked(){
        audioController?.onPauseAudio()
    }

    override fun onResumeClicked(){
        audioController?.onResumeAudio()
    }

    override fun callbackItem(position: Int, state: PlaybackStatus) {
        this.currentPosition = position
        this.state = state
        callback?.callbackItem(position, state)
    }
}