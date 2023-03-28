package com.example.mediaplayer.fragments

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.mediaplayer.R
import com.example.mediaplayer.data.PlaybackStatus
import com.example.mediaplayer.fragments.superclasses.BaseFragment
import com.example.mediaplayer.interfaces.ListContainer

class MenuFragment : BaseFragment(R.layout.fragment_menu) {
    private val viewModel: AudioViewModel by lazy { ViewModelProvider(this)[AudioViewModel::class.java] }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        viewModel.audioList.observe(viewLifecycleOwner) {
            progressBar.isVisible = false
            audioList = it
            if (savedInstanceState == null) {
                childFragmentManager.beginTransaction()
                    .replace(R.id.menu_fragment_container, HorizontalFragment())
                    .commit()
            }
            childFragmentManager.fragments.forEach { fragment ->
                if (fragment is ListContainer) {
                    fragment.setList(audioList)
                }
            }
        }
    }

    override fun callbackItem(position: Int, state: PlaybackStatus) {
        this.currentPosition = position
        this.state = state
    }
}