package com.example.mediaplayer.interfaces

import com.example.mediaplayer.models.MetaData

interface AudioServiceCallback {
    fun getMetaDataFromService(metadata: MetaData)
}