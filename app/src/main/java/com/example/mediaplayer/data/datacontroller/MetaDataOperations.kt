package com.example.mediaplayer.data.datacontroller

import com.example.mediaplayer.data.models.Audio
import com.example.mediaplayer.data.models.MetaData

class MetaDataOperations(private val foo: FavoriteObjectOperations) {
    fun getMetaData(isFavoriteFragment: Boolean, metaData: MetaData?, map: Map<Int, Audio>?): MetaData {
        val curMetaData = metaData ?: MetaData()
        return if (isFavoriteFragment) {
            if (curMetaData.isFavorite) {
                curMetaData
            } else {
                MetaData(
                    foo.findPosInFavoriteList(
                        curMetaData.currentPosition,
                        map ?: emptyMap()
                    ),
                    curMetaData.state,
                    curMetaData.isFavorite
                )
            }
        } else {
            if (!curMetaData.isFavorite) {
                curMetaData
            } else {
                MetaData(
                    foo.findPosInAllList(
                        curMetaData.currentPosition,
                        map ?: emptyMap()
                    ),
                    curMetaData.state,
                    curMetaData.isFavorite
                )
            }
        }
    }
}