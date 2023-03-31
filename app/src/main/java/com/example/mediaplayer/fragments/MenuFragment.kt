package com.example.mediaplayer.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.mediaplayer.R
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.SongMetadata
import com.example.mediaplayer.data.StorageUtils
import com.example.mediaplayer.fragments.superclasses.BaseFragment
import com.example.mediaplayer.interfaces.ListContainer

class MenuFragment : BaseFragment(R.layout.fragment_menu) {
    private val viewModel: AudioViewModel by lazy { ViewModelProvider(this)[AudioViewModel::class.java] }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.menu_fragment_container, HorizontalFragment())
                .commit()
        }

        viewModel.metaData.observe(viewLifecycleOwner) { songMetadata ->
            childFragmentManager.fragments.forEach { fragment ->
                if (fragment is ListContainer) fragment.setSongsMetadata(songMetadata)
            }
        }

        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        viewModel.audioList.observe(viewLifecycleOwner) { audioList ->
            progressBar.isVisible = false
            childFragmentManager.fragments.forEach { fragment ->
                if (fragment is ListContainer) fragment.setList(audioList)
            }
            StorageUtils(requireContext()).writeAudioList(audioList)
        }
    }

    fun getAudioMetaData(): SongMetadata? {
        return viewModel.metaData.value
    }

    fun getAudioList(): List<Audio>? {
        return viewModel.audioList.value
    }

    fun updateMetaData(songMetadata: SongMetadata) {
        viewModel.updateSongMetadata(SongMetadata(songMetadata))
    }
}