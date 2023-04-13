package com.example.mediaplayer.interfaces.myinnf.metadatacontainer

import com.example.mediaplayer.data.MetaData

interface MetaDataContainer {
    fun setMetaData(metadata: MetaData)
    fun callbackMetaData(metadata: MetaData)
}