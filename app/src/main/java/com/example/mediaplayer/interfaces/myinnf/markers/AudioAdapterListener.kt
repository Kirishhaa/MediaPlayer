package com.example.mediaplayer.interfaces.myinnf.markers

import com.example.mediaplayer.interfaces.myinnf.FavoriteObject
import com.example.mediaplayer.interfaces.myinnf.navigation.FragmentNavigator
import com.example.mediaplayer.interfaces.myinnf.metadatacontainer.MetaDataContainer
import com.example.mediaplayer.interfaces.myinnf.audiointeraction.AudioSender

interface AudioAdapterListener :
    FavoriteObject,
    FragmentNavigator,
    MetaDataContainer,
    AudioSender