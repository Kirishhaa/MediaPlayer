package com.example.mediaplayer.fragments.playerfragments.basefragments

import com.example.mediaplayer.fragments.BaseFragment
import com.example.mediaplayer.models.Audio
import com.example.mediaplayer.models.AudioEntity
import com.example.mediaplayer.models.MetaData

abstract class BaseDataFragment(resLayout: Int) : BaseFragment(resLayout) {
    @Suppress("INAPPLICABLE_JVM_NAME")
    @set:JvmName("songMetaDataField")
    @get:JvmName("songMetaDataField")
    var metaData: MetaData = MetaData()
    var audioList: List<Audio> = emptyList()
    var decoratorList: List<AudioEntity> = emptyList()
    var isFavorite = false
}