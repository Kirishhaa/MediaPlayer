package com.example.mediaplayer.data

import android.widget.CheckBox
import com.example.mediaplayer.fragments.HorizontalFragment
import com.example.mediaplayer.fragments.VerticalFragment
import com.example.mediaplayer.interfaces.markers.AudioAdapterListener

class CheckBoxSetListener(private val listener: AudioAdapterListener) {

    fun setPlayListener(
        playBox: CheckBox,
        metaData: MetaData,
        curPos: Int,
        audioList: List<Audio>
    ): Int {
        var metadata = metaData
        var callbackPosition = -1
        if (!playBox.isChecked) {
            metadata =
                MetaData(curPos, PlaybackStatus.PAUSED, listener.getIsFavoriteState())
            listener.callbackMetaData(metadata)
            listener.sendPauseAudio(metadata)
        } else {
            if (metadata.currentPosition == curPos) {
                metadata = MetaData(curPos, PlaybackStatus.PLAYING, listener.getIsFavoriteState())
                listener.callbackMetaData(metadata)
                listener.sendResumeAudio(metadata)
            } else {
                val prevPos = metadata.currentPosition
                metadata = MetaData(curPos, PlaybackStatus.PLAYING, listener.getIsFavoriteState())
                if (prevPos != -1) {
                    callbackPosition = prevPos
                }
                listener.callbackMetaData(metadata)
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
        storage: Storage
    ) {
        var metaData = metaData1
        if (checkBox.isChecked) {
            listener.addToFavorite(curPos, audioList[curPos])
        } else {
            listener.removeFromFavorite(audioList[curPos])
            if(listener.getIsFavoriteState()) {
                if (curPos == metaData.currentPosition) {
                    listener.sendStopAudio()
                    listener.callbackMetaData(MetaData())
                } else if(curPos < metaData.currentPosition) {
                    metaData = MetaData(metaData.currentPosition-1, metaData.state, metaData.isFavorite)
                    listener.callbackMetaData(metaData)
                }
                if (!listener.favoriteIsNotEmpty()) {
                    listener.navigate(HorizontalFragment())
                } else listener.navigate(VerticalFragment.onInstance(true))
            } else {
                if(curPos == metaData.currentPosition && storage.readFavorite()) {
                    listener.sendStopAudio()
                    listener.callbackMetaData(MetaData())
                }
            }
        }
        storage.writeFavoriteMap(listener.getFavoriteMap())
    }
}