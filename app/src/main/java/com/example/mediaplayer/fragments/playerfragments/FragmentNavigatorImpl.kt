package com.example.mediaplayer.fragments.playerfragments

import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.mediaplayer.R
import com.example.mediaplayer.fragments.playerfragments.listfragments.HorizontalFragment
import com.example.mediaplayer.interfaces.navigation.FragmentNavigator
import com.example.mediaplayer.storageutils.Storage

class FragmentNavigatorImpl(
    private val allFr: FrameLayout?,
    private val favFr: FrameLayout?,
    private val fragmentManager: FragmentManager,
    private val storage: Storage,
) : FragmentNavigator {
    override fun navigate(fragment: Fragment, isFavorite: Boolean) {
        if(fragment is HorizontalFragment) {
            if(storage.readFavoriteMap().isNotEmpty()) {
                fragmentManager.beginTransaction()
                    .replace(R.id.menu_fragment_favorite_audio, HorizontalFragment.onInstance(true))
                    .commit()
                favFr?.isVisible = true
            } else {
                favFr?.isVisible = false
            }
            fragmentManager.beginTransaction()
                .replace(R.id.menu_fragment_all_audio, fragment)
                .commit()
            allFr?.isVisible = true
        } else {
            fragmentManager.fragments.forEach {
                if (it is HorizontalFragment) {
                    fragmentManager.beginTransaction()
                        .remove(it)
                        .commit()
                }
            }
            val containerId =
                if (isFavorite){
                    R.id.menu_fragment_favorite_audio
                } else {
                    R.id.menu_fragment_all_audio
                }
            fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commit()

            if (isFavorite){
                allFr?.isVisible = false
            } else {
                favFr?.isVisible = false
            }
        }
    }
}