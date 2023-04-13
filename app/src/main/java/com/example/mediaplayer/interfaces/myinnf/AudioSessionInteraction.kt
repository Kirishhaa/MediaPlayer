package com.example.mediaplayer.interfaces.myinnf

import com.example.mediaplayer.data.MetaData

interface AudioSessionInteraction {
    fun getMetaDataFromService(metadata: MetaData)
}