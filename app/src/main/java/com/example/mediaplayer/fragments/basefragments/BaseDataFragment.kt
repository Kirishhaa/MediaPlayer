package com.example.mediaplayer.fragments.basefragments

import com.example.mediaplayer.data.models.Audio
import com.example.mediaplayer.data.models.AudioEntity
import com.example.mediaplayer.data.models.MetaData

abstract class BaseDataFragment(resLayout: Int): BaseFragment(resLayout) {
    @Suppress("INAPPLICABLE_JVM_NAME")
    @set:JvmName("songMetaDataField")
    @get:JvmName("songMetaDataField")
    var metaData: MetaData = MetaData()
    var audioList: List<Audio> = emptyList()
    var decoratorList: List<AudioEntity> = emptyList()
    var isFavorite = false
}