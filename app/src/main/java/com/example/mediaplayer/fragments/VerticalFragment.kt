package com.example.mediaplayer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.CustomAdapterAudio
import com.example.mediaplayer.MainActivity
import com.example.mediaplayer.R
import com.example.mediaplayer.data.Audio

class VerticalFragment: Fragment(), CustomAdapterAudio.Listener {
    private var audioList = ArrayList<Audio>()

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
        audioList = (parentFragment as MenuFragment).getList()

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_vertical)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = CustomAdapterAudio(this, CustomAdapterAudio.Type.VERTICAL)
        recycler.adapter = adapter
        adapter.setList(audioList)
    }

    override fun onItemClick(position: Int) {
        (activity as MainActivity).playAudio(position, audioList)
    }
}