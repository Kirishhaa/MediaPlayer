package com.example.mediaplayer.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mediaplayer.R
import com.example.mediaplayer.data.Audio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MenuFragment : Fragment() {
    private var audioList: List<Audio> = ArrayList()
    private val viewModel: AudioViewModel by lazy { ViewModelProvider(this)[AudioViewModel::class.java] }

    companion object {
        fun onInstance(): MenuFragment {
            return MenuFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            viewModel.audioList.observe(viewLifecycleOwner) {
                audioList = it
                childFragmentManager.fragments.forEach { fragment ->
                    if (fragment is ListContainer) {
                        fragment.setList(audioList)
                    }
                }
            }
        if(savedInstanceState==null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.menu_fragment_container, HorizontalFragment())
                .commit()
        }
    }

    fun getList() = audioList
}