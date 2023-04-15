package com.example.mediaplayer.interfaces.metadatacontainer

import com.example.mediaplayer.data.MetaData

interface MetaDataSource {
    fun getMetaData(isFavoriteFragment: Boolean): MetaData
    fun updateMetaData(metadata: MetaData)
}