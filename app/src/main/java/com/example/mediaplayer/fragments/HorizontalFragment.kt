package com.example.mediaplayer.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.fragments.superclasses.BaseListFragment

class HorizontalFragment : BaseListFragment(R.layout.fragment_list_horizontal) {

    companion object{
        fun onInstance(isFavorite: Boolean): HorizontalFragment {
            val horizontalFragment = HorizontalFragment()
            horizontalFragment.isFavorite = isFavorite
            return horizontalFragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvAsList = view.findViewById<TextView>(R.id.tv_see_as_list)
        tvAsList.setOnClickListener { navigate(VerticalFragment.onInstance(isFavorite)) }
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_music_list_horizontal)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = CustomAdapterAudio(this, CustomAdapterAudio.TypeListFragment.HORIZONTAL)
        adapter?.setAudioList(audioList, decoratorList)
        adapter?.setSongMetadata(songMetaData)
        recycler.adapter = adapter
    }

    override fun onBackPressed(): Boolean {
        return isFavorite
    }
}