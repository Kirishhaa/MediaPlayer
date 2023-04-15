package com.example.mediaplayer.interfaces.markers

import com.example.mediaplayer.interfaces.FavoriteObject
import com.example.mediaplayer.interfaces.navigation.FragmentNavigator
import com.example.mediaplayer.interfaces.metadatacontainer.MetaDataContainer
import com.example.mediaplayer.interfaces.audiointeraction.AudioSender

interface AudioAdapterListener :
    FavoriteObject,
    FragmentNavigator,
    MetaDataContainer,
    AudioSender