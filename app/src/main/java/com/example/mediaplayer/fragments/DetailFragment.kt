package com.example.mediaplayer.fragments

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.mediaplayer.R
import com.example.mediaplayer.data.*
import com.example.mediaplayer.fragments.superclasses.BaseListFragment

class DetailFragment : BaseListFragment(R.layout.fragment_detail) {

    private lateinit var storage: Storage

    private val imageArt by lazy { view?.findViewById<ImageView>(R.id.detail_image_art) }
    private val title by lazy { view?.findViewById<TextView>(R.id.detail_title) }
    private val playBox by lazy { view?.findViewById<CheckBox>(R.id.detail_play_audio) }
    private val favoriteBox by lazy { view?.findViewById<CheckBox>(R.id.detail_favorite) }
    private val shuffleBox by lazy { view?.findViewById<CheckBox>(R.id.detail_shuffle) }
    private val prevImage by lazy { view?.findViewById<ImageView>(R.id.detail_previous_audio) }
    private val nextImage by lazy { view?.findViewById<ImageView>(R.id.detail_next_audio) }

    private var detailPosition = 0

    companion object {
        fun onInstance(position: Int, isFavorite: Boolean): DetailFragment {
            val detailFragment = DetailFragment()
            detailFragment.detailPosition = position
            detailFragment.isFavorite = isFavorite
            return detailFragment
        }
    }

    override fun setMetaData(metadata: MetaData) {
        this.metaData = metadata
        if (metadata.currentPosition == detailPosition) {
            playBox?.isChecked = metadata.state == PlaybackStatus.PLAYING
        } else {
            playBox?.isChecked = false
        }
    }

    private fun updateData(checkBoxSetData: CheckBoxSetData) {
        checkBoxSetData.setData(
            imageArt = imageArt,
            title = title,
            playBox = playBox,
            curPos = detailPosition,
            metaData = metaData
        )

        favoriteBox?.isChecked =
            if (isFavorite) true else favoriteContainsAudio(audioList[detailPosition])
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailPosition = savedInstanceState?.getInt("detailPosition") ?: detailPosition

        val checkBoxSetListener = CheckBoxSetListener(this)
        val checkBoxSetData = CheckBoxSetData(decoratorList)

        storage = Storage(requireContext().applicationContext)

        updateData(checkBoxSetData)

        playBox?.setOnClickListener {
            checkBoxSetListener.setPlayListener(
                playBox = it as CheckBox,
                metaData = metaData,
                curPos = detailPosition,
                audioList = if (isFavorite) getFavoriteList() else audioList
            )
        }
        favoriteBox?.setOnClickListener {
            checkBoxSetListener.setFavoriteListener(
                checkBox = it as CheckBox,
                metaData1 = metaData,
                curPos = detailPosition,
                audioList = audioList,
                storage = storage
            )
        }

            prevImage?.setOnClickListener {
                if (detailPosition == 0) detailPosition =
                    decoratorList.size - 1 else detailPosition--
                val newMeta = MetaData(detailPosition, PlaybackStatus.PLAYING, isFavorite)
                sendPlayAudio(newMeta, audioList)
                callbackMetaData(newMeta)
                updateData(checkBoxSetData)
            }

            nextImage?.setOnClickListener {
                if (detailPosition == decoratorList.size - 1) detailPosition =
                    metaData.currentPosition + 1 else detailPosition++
                val newMeta = MetaData(detailPosition, PlaybackStatus.PLAYING, isFavorite)
                sendPlayAudio(newMeta, audioList)
                callbackMetaData(newMeta)
                updateData(checkBoxSetData)
            }
        }

        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            outState.putInt("detailPosition", detailPosition)
        }

        override fun onBackPressed(): Boolean {
            navigate(VerticalFragment.onInstance(isFavorite))
            return true
        }
    }