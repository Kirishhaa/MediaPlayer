package com.example.mediaplayer.interfaces

import com.example.mediaplayer.data.models.MetaData

interface AudioServiceCallback {
    fun getMetaDataFromService(metadata: MetaData)
}