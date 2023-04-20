package com.example.mediaplayer.dataoperations.datacontroller

import com.example.mediaplayer.models.Audio
import com.example.mediaplayer.models.MetaData

class MetaDataOperations(private val foo: FavoriteObjectOperations) {
    fun getMetaData(
        isFavoriteFragment: Boolean,
        metaData: MetaData?,
        map: Map<Int, Audio>?,
    ): MetaData {
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
                    true
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
                    false
                )
            }
        }
    }
}