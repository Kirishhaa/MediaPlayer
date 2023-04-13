package com.example.mediaplayer.interfaces.myinnf.datacontainer

import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.AudioEntity
import com.example.mediaplayer.data.MetaData

interface AudioData {
    var isFavorite: Boolean
    var decoratorList: List<AudioEntity>
    var audioList: List<Audio>
    @Suppress("INAPPLICABLE_JVM_NAME")
    @set:JvmName("songMetaDataField")
    @get:JvmName("songMetaDataField")
    var metaData: MetaData
}