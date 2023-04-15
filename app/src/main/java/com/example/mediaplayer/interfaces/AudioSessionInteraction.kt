package com.example.mediaplayer.interfaces

import com.example.mediaplayer.data.MetaData

interface AudioSessionInteraction {
    fun getMetaDataFromService(metadata: MetaData)
}