package com.example.mediaplayer.fragments

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.example.mediaplayer.R
import com.example.mediaplayer.data.PlaybackStatus
import com.example.mediaplayer.data.MetaData
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

    override fun setMetaData(metadata: MetaData) {
        this.metaData = metadata
        if (metadata.currentPosition == detailPosition) {
            playBox?.isChecked = metadata.state == PlaybackStatus.PLAYING
        } else {
            playBox?.isChecked = false
        }
    }

    private fun updateData() {
        if(decoratorList[detailPosition].bitmap==null) imageArt?.setImageResource(R.drawable.ic_empty_music_card)
        else imageArt?.setImageBitmap(decoratorList[detailPosition].bitmap)

        title?.text = decoratorList[detailPosition].title

        if (metaData.state == PlaybackStatus.PLAYING) {
            playBox?.isChecked = metaData.currentPosition == detailPosition
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
                metaData = MetaData(detailPosition, PlaybackStatus.PAUSED, isFavorite)
                callbackMetaData(metaData)
                sendPauseAudio(metaData)
            } else {
                if (metaData.currentPosition == detailPosition) {
                    metaData = MetaData(detailPosition, PlaybackStatus.PLAYING, isFavorite)
                    callbackMetaData(metaData)
                    sendResumeAudio(metaData)
                } else {
                    metaData = MetaData(detailPosition, PlaybackStatus.PLAYING, isFavorite)
                    callbackMetaData(metaData)
                    val list = if (isFavorite) getFavoriteList() ?: emptyList() else audioList
                    sendPlayAudio(metaData, list)
                }
            }
        }

        favoriteBox?.setOnClickListener {
            val checkBox = it as CheckBox
            if (checkBox.isChecked) {
                addToFavorite(detailPosition, audioList[detailPosition])
            } else {
                removeFromFavorite(audioList[detailPosition])
                if (!favoriteIsNotEmpty()) {
                    if(storage.readFavorite()) {
                        sendStopAudio()
                        callbackMetaData(MetaData())
                    }
                    if(isFavorite) navigate(HorizontalFragment())
                } else {
                    if ((playBox!!.isChecked || metaData.currentPosition == detailPosition)) {
                        if(audioList.size-1 == detailPosition){
                            val newMetaData = MetaData(0, PlaybackStatus.PLAYING, isFavorite)
                            callbackMetaData(newMetaData)
                          sendPlayAudio(newMetaData, audioList)
                        } else {
                            sendPlayAudio(metaData, audioList)
                        }
                        if(isFavorite) navigate(VerticalFragment.onInstance(isFavorite))
                    } else {
                        if(storage.readFavorite()) {
                            callbackMetaData(MetaData())
                            sendStopAudio()
                        }
                        if(isFavorite) navigate(VerticalFragment.onInstance(isFavorite))
                    }
                }
            }
            storage.writeFavoriteMap(getFavoriteMap() ?: emptyMap())
        }

        prevImage?.setOnClickListener {
            if(detailPosition==0) detailPosition = decoratorList.size-1 else detailPosition--
            val newMeta = MetaData(detailPosition, PlaybackStatus.PLAYING, isFavorite)
            sendPlayAudio(newMeta, audioList)
            callbackMetaData(newMeta)
            updateData()
        }

        nextImage?.setOnClickListener {
            if(detailPosition==decoratorList.size-1) detailPosition = metaData.currentPosition+1 else detailPosition++
            val newMeta = MetaData(detailPosition, PlaybackStatus.PLAYING, isFavorite)
            sendPlayAudio(newMeta, audioList)
            callbackMetaData(newMeta)
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