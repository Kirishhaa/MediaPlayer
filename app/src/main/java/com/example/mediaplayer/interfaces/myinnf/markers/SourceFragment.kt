package com.example.mediaplayer.interfaces.myinnf.markers

import com.example.mediaplayer.interfaces.myinnf.listcontainer.AllListContainer
import com.example.mediaplayer.interfaces.myinnf.FavoriteObject
import com.example.mediaplayer.interfaces.myinnf.listcontainer.FavoriteListContainer
import com.example.mediaplayer.interfaces.myinnf.navigation.FragmentNavigator
import com.example.mediaplayer.interfaces.myinnf.metadatacontainer.MetaDataSource

interface SourceFragment :
    MetaDataSource,
    AllListContainer,
    FavoriteObject,
    FavoriteListContainer,
    FragmentNavigator