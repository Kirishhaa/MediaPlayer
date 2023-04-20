package com.example.mediaplayer.fragments.playerfragments.listfragments

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.example.mediaplayer.R
import com.example.mediaplayer.dataoperations.xml.XMLAudioDecorator
import com.example.mediaplayer.dataoperations.xml.XMLListenerSetter
import com.example.mediaplayer.interfaces.ProgressBarContainer
import com.example.mediaplayer.fragments.playerfragments.basefragments.BaseListFragment
import com.example.mediaplayer.interfaces.ProgressBarSource
import com.example.mediaplayer.interfaces.markers.SourceFragment
import com.example.mediaplayer.models.MetaData
import com.example.mediaplayer.models.PlaybackStatus
import com.example.mediaplayer.storageutils.Storage
import com.google.android.material.progressindicator.LinearProgressIndicator

class DetailFragment : BaseListFragment(R.layout.fragment_detail),
    ProgressBarContainer {

    override var toolbarTitle: String = "Detail"

    private lateinit var storage: Storage

    private val imageArt by lazy { view?.findViewById<ImageView>(R.id.detail_image_art) }
    private val title by lazy { view?.findViewById<TextView>(R.id.detail_title) }
    private var playBox: CheckBox? = null
    private val favoriteBox by lazy { view?.findViewById<CheckBox>(R.id.detail_favorite) }
    private val shuffleBox by lazy { view?.findViewById<CheckBox>(R.id.detail_shuffle) }
    private var progressBar: LinearProgressIndicator? = null
    private var progressBarTime: TextView? = null
    private val prevImage by lazy { view?.findViewById<ImageView>(R.id.detail_previous_audio) }
    private val nextImage by lazy { view?.findViewById<ImageView>(R.id.detail_next_audio) }

    private var detailPosition = 0
    private var currentAudioTime: Pair<Int, Int> = Pair(0, 0)
    private var xmlAudioDecorator = XMLAudioDecorator()

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
        xmlAudioDecorator.setImageArt(imageArt, detailPosition)
        xmlAudioDecorator.setTitle(title, detailPosition)
        xmlAudioDecorator.setPlayBox(playBox, metaData, detailPosition)
        xmlAudioDecorator.setFavoriteBox(favoriteBox, audioList, detailPosition)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //must be changed impl of saving states fragments
        if (savedInstanceState != null) navigate(this)
        detailPosition = savedInstanceState?.getInt("detailPosition") ?: detailPosition
        isFavorite = savedInstanceState?.getBoolean("isFavorite") ?: isFavorite

        progressBarTime = view.findViewById(R.id.detail_progress_bar_time)
        progressBar = view.findViewById(R.id.detail_progress_bar)
        playBox = view.findViewById(R.id.detail_play_audio)
        val xmlListenerSetter = XMLListenerSetter(this)
        xmlAudioDecorator = XMLAudioDecorator(decoratorList, this)

        storage = Storage(requireContext().applicationContext)

        setCurrentTime((parentFragment as ProgressBarSource).getAudioCurrentTime())
        updateData()

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
            updateData()
        }

        nextImage?.setOnClickListener {
            if (detailPosition == decoratorList.size - 1) detailPosition =
                0 else detailPosition++
            val newMeta = MetaData(detailPosition, PlaybackStatus.PLAYING, isFavorite)
            sendPlayAudio(newMeta, audioList)
            updateData()
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

    override fun setCurrentTime(data: Pair<Int, Int>) {
        if (((isFavorite && metaData.isFavorite)
                    || (!isFavorite && !metaData.isFavorite))
            && metaData.currentPosition == detailPosition
        ) {
            if (progressBar != null) {
                xmlAudioDecorator.setProgressBar(progressBar, progressBarTime, data)
            }
        }
    }
}