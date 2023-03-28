package com.example.mediaplayer.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.PlaybackStatus
import com.example.mediaplayer.data.StorageUtils
import com.example.mediaplayer.fragments.superclasses.BaseListFragment

class HorizontalFragment : BaseListFragment(R.layout.fragment_list_horizontal) {
    private lateinit var adapter: HorizontalAdapterAudio

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val asList = view.findViewById<TextView>(R.id.tv_see_as_list)
        asList.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.menu_fragment_container, VerticalFragment())
                .commit()
        }
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_music_list_horizontal)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = HorizontalAdapterAudio(this, currentPosition)
        adapter.setList(audioList)
        adapter.setCurrentData(currentPosition, state)
        recycler.adapter = adapter
        StorageUtils(requireContext()).writeAudioList(audioList)
    }

    override fun setList(audioList: List<Audio>) {
        adapter.setList(audioList)
        this.audioList = audioList
    }
}