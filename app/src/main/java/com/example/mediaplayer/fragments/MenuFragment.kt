package com.example.mediaplayer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mediaplayer.R
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.fragments.marks.ListContainer
import com.example.mediaplayer.fragments.marks.PositionCallbacker

class MenuFragment : Fragment(), PositionCallbacker {
    private val viewModel: AudioViewModel by lazy { ViewModelProvider(this)[AudioViewModel::class.java] }
    var audioList: List<Audio> = ArrayList()
        private set
    var currentPosition: Int = -1
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            viewModel.audioList.observe(viewLifecycleOwner) {
                if(it!=null) {
                    audioList = it
                    childFragmentManager.fragments.forEach { fragment ->
                        if (fragment is ListContainer) {
                            fragment.setList(audioList)
                        }
                    }
                }
            }
        if(savedInstanceState==null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.menu_fragment_container, HorizontalFragment())
                .commit()
        }
    }

    override fun callbackPosition(position: Int) {
        this.currentPosition = position
    }
}