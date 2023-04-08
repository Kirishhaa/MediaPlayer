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

    private lateinit var playBox: CheckBox
    private lateinit var favoriteBox: CheckBox
    private lateinit var storage: Storage
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
            playBox.isChecked = songMetadata.state == PlaybackStatus.PLAYING
        } else {
            playBox.isChecked = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storage = Storage(requireContext().applicationContext)


        val curAudio = audioList[detailPosition]


        val imageArt = view.findViewById<ImageView>(R.id.detail_image_art)
        imageArt.setImageBitmap(curAudio.imageArt)

        val title = view.findViewById<TextView>(R.id.detail_title)
        title.text = curAudio.title

        playBox = view.findViewById(R.id.detail_play_audio)
        if (songMetaData.state == PlaybackStatus.PLAYING) {
            playBox.isChecked = songMetaData.currentPosition == detailPosition
        } else {
            playBox.isChecked = false
        }

        playBox.setOnClickListener {
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
                    onPlayClicked(songMetaData, list)
                }
            }
        }

        favoriteBox = view.findViewById(R.id.detail_favorite)

        favoriteBox.isChecked = if (isFavorite) true else favoriteContains(detailPosition)

        favoriteBox.setOnClickListener {
            val checkBox = it as CheckBox
            if (checkBox.isChecked) {
                addFavoriteAudio(audioList[detailPosition], detailPosition)
            } else {
                removeFavoriteAudio(audioList[detailPosition])
                if (!favoriteListIsNotEmpty()) {
                    if(storage.readFavorite()) onStopClicked()
                    if(isFavorite) navigate(HorizontalFragment())
                } else {
                    if ((playBox.isChecked || songMetaData.currentPosition == detailPosition)) {
                        if(audioList.size-1 == detailPosition){
                            val newMetadata = SongMetadata(0, PlaybackStatus.PLAYING, isFavorite)
                            callbackMetadata(newMetadata)
                          onPlayClicked(newMetadata, audioList)
                        } else {
                            onPlayClicked(songMetaData, audioList)
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
            storage.writeFavoriteAudioList(getFavoriteList() ?: emptyList())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isFavorite", favoriteBox.isChecked)
    }

    override fun onBackPressed(): Boolean {
        navigate(VerticalFragment.onInstance(isFavorite))
        return true
    }
}