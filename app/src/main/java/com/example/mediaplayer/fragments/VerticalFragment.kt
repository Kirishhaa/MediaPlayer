package com.example.mediaplayer.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.data.SongMetadata
import com.example.mediaplayer.fragments.superclasses.BaseListFragment

class VerticalFragment : BaseListFragment(R.layout.fragment_list_vertical) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_vertical)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = CustomAdapterAudio(this, CustomAdapterAudio.TypeListFragment.VERTICAL)
        adapter.setAudioList(audioList)
        adapter.setSongMetadata(audioMetaData ?: SongMetadata())
        recycler.adapter = adapter
    }
}