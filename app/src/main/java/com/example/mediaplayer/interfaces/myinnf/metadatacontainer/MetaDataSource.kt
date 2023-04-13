package com.example.mediaplayer.interfaces.myinnf.metadatacontainer

import com.example.mediaplayer.data.MetaData

interface MetaDataSource {
    fun getMetaData(): MetaData
    fun updateMetaData(metadata: MetaData)
}