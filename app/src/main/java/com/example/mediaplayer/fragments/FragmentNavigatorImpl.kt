package com.example.mediaplayer.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.mediaplayer.R
import com.example.mediaplayer.storageutils.Storage
import com.example.mediaplayer.fragments.listfragments.HorizontalFragment
import com.example.mediaplayer.interfaces.navigation.FragmentNavigator

class FragmentNavigatorImpl(
    private val fragmentManager: FragmentManager,
    private val storage: Storage,
) : FragmentNavigator {
    override fun navigate(fragment: Fragment) {
        if (fragment is HorizontalFragment) {
            if (storage.readFavoriteMap().isNotEmpty())
                fragmentManager.beginTransaction()
                    .replace(R.id.menu_fragment_favorite_audio, HorizontalFragment.onInstance(true))
                    .commit()
        } else {
            fragmentManager.fragments.forEach {
                if (it is HorizontalFragment) {
                    fragmentManager.beginTransaction()
                        .remove(it)
                        .commit()
                    return@forEach
                }
            }
        }
        fragmentManager.beginTransaction()
            .replace(R.id.menu_fragment_all_audio, fragment)
            .commit()
    }
}