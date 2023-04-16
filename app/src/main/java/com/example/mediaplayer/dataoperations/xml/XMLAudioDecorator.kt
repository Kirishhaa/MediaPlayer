package com.example.mediaplayer.dataoperations.xml

import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.example.mediaplayer.R
import com.example.mediaplayer.models.Audio
import com.example.mediaplayer.models.AudioEntity
import com.example.mediaplayer.models.MetaData
import com.example.mediaplayer.models.PlaybackStatus
import com.example.mediaplayer.interfaces.markers.AudioAdapterListener

class XMLAudioDecorator(
    private var decoratorList: List<AudioEntity> = emptyList(),
    private val listener: AudioAdapterListener? = null,
) {

    fun setDecoratorList(decoratorList: List<AudioEntity>) {
        this.decoratorList = decoratorList
    }

    fun setData(
        imageArt: ImageView? = null,
        title: TextView? = null,
        playBox: CheckBox? = null,
        favoriteBox: CheckBox? = null,
        curPos: Int,
        metaData: MetaData,
        audioList: List<Audio>,
    ) {
        if (decoratorList[curPos].bitmap == null) imageArt?.setImageResource(R.drawable.ic_empty_music_card)
        else imageArt?.setImageBitmap(decoratorList[curPos].bitmap)

        title?.text = decoratorList[curPos].title

        if (metaData.state == PlaybackStatus.PLAYING) {
            playBox?.isChecked = metaData.currentPosition == curPos
        } else {
            playBox?.isChecked = false
        }

        favoriteBox?.isChecked =
            if (listener?.getIsFavoriteState() == true) true else listener?.favoriteContainsAudio(
                audioList[curPos]
            ) ?: false
    }
}