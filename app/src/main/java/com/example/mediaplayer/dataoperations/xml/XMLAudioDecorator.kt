package com.example.mediaplayer.dataoperations.xml

import android.os.Looper
import android.os.Handler
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.example.mediaplayer.R
import com.example.mediaplayer.models.Audio
import com.example.mediaplayer.models.AudioEntity
import com.example.mediaplayer.models.MetaData
import com.example.mediaplayer.models.PlaybackStatus
import com.example.mediaplayer.interfaces.markers.AudioAdapterListener
import com.google.android.material.progressindicator.LinearProgressIndicator

class XMLAudioDecorator(
    private var decoratorList: List<AudioEntity> = emptyList(),
    private val listener: AudioAdapterListener? = null,
) {
    private val handler = Handler(Looper.getMainLooper())

    fun setDecoratorList(decoratorList: List<AudioEntity>) {
        this.decoratorList = decoratorList
    }

    fun setImageArt(
        imageArt: ImageView?,
        curPos: Int
    ) {
        handler.post {
            if (decoratorList[curPos].bitmap == null) imageArt?.setImageResource(R.drawable.ic_empty_music_card)
            else imageArt?.setImageBitmap(decoratorList[curPos].bitmap)
        }
    }

    fun setTitle(title: TextView?, curPos: Int) {
        handler.post {
            title?.text = decoratorList[curPos].title
        }
    }

    fun setPlayBox(playBox: CheckBox?,metaData: MetaData, curPos: Int) {
        handler.post {
            if (metaData.state == PlaybackStatus.PLAYING) {
                playBox?.isChecked = metaData.currentPosition == curPos
            } else {
                playBox?.isChecked = false
            }
        }
    }

    fun setFavoriteBox(favoriteBox: CheckBox?, audioList: List<Audio>, curPos: Int) {
        handler.post {
            favoriteBox?.isChecked =
                if (listener?.getIsFavoriteState() == true) true else listener?.favoriteContainsAudio(
                    audioList[curPos]
                ) ?: false
        }
    }

    fun setProgressBar(progressIndicator: LinearProgressIndicator?,
                       progressBarTime: TextView?,
                       data: Pair<Int, Int>) {
        handler.post {
            progressIndicator?.max = data.second
            progressIndicator?.progress = data.first
            progressBarTime?.text = getTimeString(data.first)
        }
    }


    private fun getTimeString(millis: Int): String {
        val allSecond = millis/1000
        val seconds = allSecond%60
        val minutes = allSecond/60
        val hours = allSecond/3600

        val builder = StringBuilder()
        builder.append(if(hours<10) "0${hours}:" else "$hours:")
            .append(if(minutes<10) "0${minutes}:" else "$minutes:")
            .append(if(seconds<10) "0${seconds}" else "$seconds")

        return builder.toString()
    }
}