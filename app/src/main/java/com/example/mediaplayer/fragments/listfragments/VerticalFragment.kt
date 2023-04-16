package com.example.mediaplayer.fragments.listfragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.fragments.CustomAdapterAudio
import com.example.mediaplayer.fragments.basefragments.BaseListFragment

class VerticalFragment : BaseListFragment(R.layout.fragment_list_vertical) {

    companion object{
        fun onInstance(isFavorite: Boolean): VerticalFragment {
            val verticalFragment = VerticalFragment()
            verticalFragment.isFavorite = isFavorite
            return verticalFragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_vertical)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = CustomAdapterAudio(this, CustomAdapterAudio.TypeListFragment.VERTICAL)
        setList(audioList, decoratorList)
        setMetaData(metaData)
        recycler.adapter = adapter
    }

    override fun onBackPressed(): Boolean {
        navigate(HorizontalFragment())
        return true
    }
}