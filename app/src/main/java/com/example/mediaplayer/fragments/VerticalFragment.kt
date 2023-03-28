package com.example.mediaplayer.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.PlaybackStatus
import com.example.mediaplayer.data.StorageUtils
import com.example.mediaplayer.fragments.superclasses.BaseListFragment

class VerticalFragment: BaseListFragment(R.layout.fragment_list_vertical) {
    private lateinit var adapter: VerticalAdapterAudio

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_vertical)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = VerticalAdapterAudio(this, currentPosition)
        adapter.setList(audioList)
        adapter.setCurrentData(currentPosition, state)
        recycler.adapter = adapter
        StorageUtils(requireContext()).writeAudioList(audioList)
    }

    override fun setList(audioList: List<Audio>) {
        this.audioList = audioList
        adapter.setList(audioList)
    }
}