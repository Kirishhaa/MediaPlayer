package com.example.mediaplayer.fragments.listfragments

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.example.mediaplayer.R
import com.example.mediaplayer.storageutils.Storage
import com.example.mediaplayer.models.MetaData
import com.example.mediaplayer.models.PlaybackStatus
import com.example.mediaplayer.fragments.basefragments.BaseListFragment
import com.example.mediaplayer.dataoperations.xml.XMLAudioDecorator
import com.example.mediaplayer.dataoperations.xml.XMLListenerSetter

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

    private fun updateData(xmlAudioDecorator: XMLAudioDecorator) {
        xmlAudioDecorator.setData(
            imageArt = imageArt,
            title = title,
            playBox = playBox,
            favoriteBox = favoriteBox,
            curPos = detailPosition,
            metaData = metaData,
            audioList = audioList
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailPosition = savedInstanceState?.getInt("detailPosition") ?: detailPosition
        isFavorite = savedInstanceState?.getBoolean("isFavorite") ?: isFavorite

        val xmlListenerSetter = XMLListenerSetter(this)
        val xmlAudioDecorator = XMLAudioDecorator(decoratorList, this)

        storage = Storage(requireContext().applicationContext)

        updateData(xmlAudioDecorator)

        playBox?.setOnClickListener {
            xmlListenerSetter.setPlayListener(
                playBox = it as CheckBox,
                metaData = metaData,
                curPos = detailPosition,
                audioList = if (isFavorite) getFavoriteList() else audioList
            )
        }
        favoriteBox?.setOnClickListener {
            xmlListenerSetter.setFavoriteListener(
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
            updateData(xmlAudioDecorator)
        }

        nextImage?.setOnClickListener {
            if (detailPosition == decoratorList.size - 1) detailPosition =
                0 else detailPosition++
            val newMeta = MetaData(detailPosition, PlaybackStatus.PLAYING, isFavorite)
            sendPlayAudio(newMeta, audioList)
            callbackMetaData(newMeta)
            updateData(xmlAudioDecorator)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("detailPosition", detailPosition)
        outState.putBoolean("isFavorite", isFavorite)
    }

    override fun onBackPressed(): Boolean {
        navigate(VerticalFragment.onInstance(isFavorite))
        return true
    }
}