package com.example.mediaplayer.interfaces

import androidx.fragment.app.Fragment
import com.example.mediaplayer.fragments.superclasses.BaseListFragment

interface FragmentNavigator {
    fun navigate(fragment: BaseListFragment)
}