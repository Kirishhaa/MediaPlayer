package com.example.mediaplayer.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.data.SongMetadata
import com.example.mediaplayer.fragments.superclasses.BaseListFragment

class HorizontalFragment : BaseListFragment(R.layout.fragment_list_horizontal) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvAsList = view.findViewById<TextView>(R.id.tv_see_as_list)
        tvAsList.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.menu_fragment_container, VerticalFragment())
                .commit()
        }
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_music_list_horizontal)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = CustomAdapterAudio(this, CustomAdapterAudio.TypeListFragment.HORIZONTAL)
        adapter.setAudioList(audioList)
        adapter.setSongMetadata(audioMetaData ?: SongMetadata())
        recycler.adapter = adapter
    }
}