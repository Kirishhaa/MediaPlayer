package com.example.mediaplayer.fragments.superclasses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.PlaybackStatus
import com.example.mediaplayer.interfaces.Callback

abstract class BaseFragment(private val resLayout: Int) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(resLayout, container, false)
    }
}