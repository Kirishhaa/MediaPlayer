package com.example.mediaplayer.data

import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.example.mediaplayer.R

class CheckBoxSetData(private var decoratorList: List<AudioEntity>) {

    constructor() : this(emptyList())

    fun setDecoratorList(decoratorList: List<AudioEntity>) {
        this.decoratorList = decoratorList
    }

   fun setData(imageArt: ImageView?,
                           title: TextView?,
                           playBox: CheckBox?,
                           curPos: Int,
                           metaData: MetaData
    ) {
        if (decoratorList[curPos].bitmap == null) imageArt?.setImageResource(R.drawable.ic_empty_music_card)
        else imageArt?.setImageBitmap(decoratorList[curPos].bitmap)

        title?.text = decoratorList[curPos].title

        if (metaData.state == PlaybackStatus.PLAYING) {
            playBox?.isChecked = metaData.currentPosition == curPos
        } else {
            playBox?.isChecked = false
        }
    }
}