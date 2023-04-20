package com.example.mediaplayer.dataoperations.xml

import android.util.Log
import android.widget.CheckBox
import com.example.mediaplayer.models.Audio
import com.example.mediaplayer.models.MetaData
import com.example.mediaplayer.models.PlaybackStatus
import com.example.mediaplayer.storageutils.Storage
import com.example.mediaplayer.fragments.playerfragments.listfragments.HorizontalFragment
import com.example.mediaplayer.fragments.playerfragments.listfragments.VerticalFragment
import com.example.mediaplayer.interfaces.markers.AudioAdapterListener

class XMLListenerSetter(private val listener: AudioAdapterListener) {

    fun setPlayListener(
        playBox: CheckBox,
        metaData: MetaData,
        curPos: Int,
        audioList: List<Audio>,
    ): Int {
        var metadata = metaData
        var callbackPosition = -1
        if (!playBox.isChecked) {
            metadata =
                MetaData(curPos, PlaybackStatus.PAUSED, listener.getIsFavoriteState())
            listener.sendPauseAudio(metadata)
        } else {
            if (metadata.currentPosition == curPos) {
                metadata = MetaData(curPos, PlaybackStatus.PLAYING, listener.getIsFavoriteState())
                listener.sendResumeAudio(metadata)
            } else {
                val prevPos = metadata.currentPosition
                metadata = MetaData(curPos, PlaybackStatus.PLAYING, listener.getIsFavoriteState())
                if (prevPos != -1) {
                    callbackPosition = prevPos
                }
                listener.sendPlayAudio(metadata, audioList)
            }
        }
        return callbackPosition
    }

    fun setFavoriteListener(
        checkBox: CheckBox,
        metaData1: MetaData,
        curPos: Int,
        audioList: List<Audio>,
        storage: Storage,
    ) {
        if (checkBox.isChecked) {
            listener.addToFavorite(curPos, audioList[curPos])
        } else {
            listener.removeFromFavorite(audioList[curPos])
            if (listener.getIsFavoriteState()) {
                if (curPos == metaData1.currentPosition) {
                    listener.sendStopAudio()
                }
                if (!listener.favoriteIsNotEmpty()) {
                    listener.navigate(HorizontalFragment())
                } else listener.navigate(VerticalFragment.onInstance(true))
            } else {
                if (curPos == metaData1.currentPosition && storage.readFavorite()) {
                    listener.sendStopAudio()
                }
            }
        }
        storage.writeFavoriteMap(listener.getFavoriteMap())
    }
}