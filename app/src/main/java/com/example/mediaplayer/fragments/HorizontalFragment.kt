package com.example.mediaplayer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.MainActivity
import com.example.mediaplayer.R
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.databinding.FragmentListHorizontalBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class HorizontalFragment : Fragment(), CustomAdapterAudio.Listener, ListContainer {
    private var binding: FragmentListHorizontalBinding? = null
    private var audioList: List<Audio> = ArrayList()
    private val adapter = CustomAdapterAudio(this, TypeFragmentList.HORIZONTAL)

    companion object {
        fun onInstance(): HorizontalFragment {
            return HorizontalFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentListHorizontalBinding.inflate(layoutInflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val asList = binding!!.tvSeeAsList
        asList.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.menu_fragment_container, VerticalFragment.onInstance())
                .commit()
        }
        val recycler = binding!!.recyclerMusicListHorizontal
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recycler.adapter = adapter
        //coroutine?
            audioList = (parentFragment as MenuFragment).getList()
            adapter.setList(audioList)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onItemClick(position: Int) {
        (activity as MainActivity).playAudio(position, audioList)
    }

    override fun setList(audioList: List<Audio>) {
        adapter.setList(audioList)
        this.audioList = audioList
    }
}