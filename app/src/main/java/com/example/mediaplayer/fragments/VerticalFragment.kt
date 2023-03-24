package com.example.mediaplayer.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.MainActivity
import com.example.mediaplayer.R
import com.example.mediaplayer.data.Audio

class VerticalFragment: Fragment(), CustomAdapterAudio.Listener, ListContainer {
    private var audioList: List<Audio> = ArrayList()
    private val adapter = CustomAdapterAudio(this, TypeFragmentList.VERTICAL)


    companion object{
        fun onInstance(): VerticalFragment {
            return VerticalFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_vertical, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_vertical)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = adapter

        //coroutine?
        audioList = (parentFragment as MenuFragment).getList()
        adapter.setList(audioList)
    }

    override fun onItemClick(position: Int) {
        (activity as MainActivity).playAudio(position, audioList)
    }

    override fun setList(audioList: List<Audio>) {
        this.audioList = audioList
        adapter.setList(audioList)
    }
}