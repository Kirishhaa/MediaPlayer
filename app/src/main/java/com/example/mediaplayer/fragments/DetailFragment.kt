package com.example.mediaplayer.fragments

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.example.mediaplayer.R
import com.example.mediaplayer.data.PlaybackStatus
import com.example.mediaplayer.data.SongMetadata
import com.example.mediaplayer.data.Storage
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

    override fun setAllListMetaData(songMetadata: SongMetadata) {
        this.songMetaData = songMetadata
        if (songMetadata.currentPosition == detailPosition) {
            playBox?.isChecked = songMetadata.state == PlaybackStatus.PLAYING
        } else {
            playBox?.isChecked = false
        }
    }

    private fun updateData() {
        if(decoratorList[detailPosition].bitmap==null) imageArt?.setImageResource(R.drawable.ic_empty_music_card)
        else imageArt?.setImageBitmap(decoratorList[detailPosition].bitmap)

        title?.text = decoratorList[detailPosition].title

        if (songMetaData.state == PlaybackStatus.PLAYING) {
            playBox?.isChecked = songMetaData.currentPosition == detailPosition
        } else {
            playBox?.isChecked = false
        }

        favoriteBox?.isChecked = if (isFavorite) true else favoriteContains(detailPosition)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailPosition = savedInstanceState?.getInt("detailPosition") ?: detailPosition

        storage = Storage(requireContext().applicationContext)

        updateData()

        playBox?.setOnClickListener {
            val checkBox = it as CheckBox
            if (!checkBox.isChecked) {
                songMetaData = SongMetadata(detailPosition, PlaybackStatus.PAUSED, isFavorite)
                callbackMetadata(songMetaData)
                onPauseClicked(songMetaData)
            } else {
                if (songMetaData.currentPosition == detailPosition) {
                    songMetaData = SongMetadata(detailPosition, PlaybackStatus.PLAYING, isFavorite)
                    callbackMetadata(songMetaData)
                    onResumeClicked(songMetaData)
                } else {
                    songMetaData = SongMetadata(detailPosition, PlaybackStatus.PLAYING, isFavorite)
                    callbackMetadata(songMetaData)
                    val list = if (isFavorite) getFavoriteList() ?: emptyList() else audioList
                    onPlayClicked(songMetaData, list, getFavoriteMap())
                }
            }
        }

        favoriteBox?.setOnClickListener {
            val checkBox = it as CheckBox
            if (checkBox.isChecked) {
                addFavoriteAudio(audioList[detailPosition], detailPosition)
            } else {
                removeFavoriteAudio(audioList[detailPosition])
                if (!favoriteListIsNotEmpty()) {
                    if(storage.readFavorite()) {
                        onStopClicked()
                        callbackMetadata(SongMetadata())
                    }
                    if(isFavorite) navigate(HorizontalFragment())
                } else {
                    if ((playBox!!.isChecked || songMetaData.currentPosition == detailPosition)) {
                        if(audioList.size-1 == detailPosition){
                            val newMetadata = SongMetadata(0, PlaybackStatus.PLAYING, isFavorite)
                            callbackMetadata(newMetadata)
                          onPlayClicked(newMetadata, audioList, getFavoriteMap())
                        } else {
                            onPlayClicked(songMetaData, audioList, getFavoriteMap())
                        }
                        if(isFavorite) navigate(VerticalFragment.onInstance(isFavorite))
                    } else {
                        if(storage.readFavorite()) {
                            callbackMetadata(SongMetadata())
                            onStopClicked()
                        }
                        if(isFavorite) navigate(VerticalFragment.onInstance(isFavorite))
                    }
                }
            }
            storage.writeFavoriteMap(getFavoriteMap() ?: emptyMap())
        }

        prevImage?.setOnClickListener {
            if(detailPosition==0) detailPosition = decoratorList.size-1 else detailPosition--
            val newMeta = SongMetadata(detailPosition, PlaybackStatus.PLAYING, isFavorite)
            onPlayClicked(newMeta, audioList, getFavoriteMap())
            callbackMetadata(newMeta)
            updateData()
        }

        nextImage?.setOnClickListener {
            if(detailPosition==decoratorList.size-1) detailPosition = songMetaData.currentPosition+1 else detailPosition++
            val newMeta = SongMetadata(detailPosition, PlaybackStatus.PLAYING, isFavorite)
            onPlayClicked(newMeta, audioList, getFavoriteMap())
            callbackMetadata(newMeta)
            updateData()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isFavorite", favoriteBox!!.isChecked)
        outState.putInt("detailPosition", detailPosition)
    }

    override fun onBackPressed(): Boolean {
        navigate(VerticalFragment.onInstance(isFavorite))
        return true
    }
}