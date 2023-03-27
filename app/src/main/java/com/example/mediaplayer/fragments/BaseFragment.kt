package com.example.mediaplayer.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.fragments.marks.AdapterListener
import com.example.mediaplayer.fragments.marks.AudioController
import com.example.mediaplayer.fragments.marks.ListContainer
import com.example.mediaplayer.fragments.marks.PositionCallbacker

abstract class BaseFragment(private val resLayout: Int): Fragment(), AdapterListener, ListContainer {
    protected var audioList: List<Audio> = ArrayList()
    protected var currentPosition = -1
    private var audioController: AudioController? = null
    private var positionCallbacker: PositionCallbacker? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        audioController = context as AudioController
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        positionCallbacker = parentFragment as PositionCallbacker
        return inflater.inflate(resLayout, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        positionCallbacker = null
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

    override fun callbackPosition(position: Int) {
        this.currentPosition = position
        positionCallbacker?.callbackPosition(position)
    }
}