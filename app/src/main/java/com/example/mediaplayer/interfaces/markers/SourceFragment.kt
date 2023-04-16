package com.example.mediaplayer.interfaces.markers

import com.example.mediaplayer.interfaces.FavoriteObject
import com.example.mediaplayer.interfaces.listcontainer.AllListContainer
import com.example.mediaplayer.interfaces.listcontainer.FavoriteListContainer
import com.example.mediaplayer.interfaces.navigation.FragmentNavigator
import com.example.mediaplayer.interfaces.metadatacontainer.MetaDataSource

interface SourceFragment :
    MetaDataSource,
    AllListContainer,
    FavoriteObject,
    FavoriteListContainer,
    FragmentNavigator