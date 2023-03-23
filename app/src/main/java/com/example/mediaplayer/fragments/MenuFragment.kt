package com.example.mediaplayer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mediaplayer.AudioViewModel
import com.example.mediaplayer.R
import com.example.mediaplayer.data.Audio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MenuFragment: Fragment() {

    private val viewModel by lazy { ViewModelProvider(this)[AudioViewModel::class.java] }
    private var audioList = ArrayList<Audio>()

    companion object{
        fun onInstance(): MenuFragment{
            return MenuFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            viewModel.audioList.observe(viewLifecycleOwner) {
                childFragmentManager
                    .beginTransaction()
                    .replace(R.id.menu_fragment_container, HorizontalFragment.onInstance())
                    .commit()
                audioList = it
            }
    }

    fun getList():ArrayList<Audio>{
        return audioList
    }
}