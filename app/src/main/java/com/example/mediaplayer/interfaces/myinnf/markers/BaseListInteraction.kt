package com.example.mediaplayer.interfaces.myinnf.markers

import com.example.mediaplayer.interfaces.myinnf.datacontainer.AudioData
import com.example.mediaplayer.interfaces.myinnf.listcontainer.FavoriteListContainer
import com.example.mediaplayer.interfaces.myinnf.listcontainer.ListContainer
import com.example.mediaplayer.interfaces.myinnf.navigation.FragmentBackPressed
import com.example.mediaplayer.interfaces.myinnf.navigation.FragmentNavigator

interface BaseListInteraction :
    FragmentNavigator,
    FragmentBackPressed,
    AudioAdapterListener,
    AudioData,
    ListContainer,
    FavoriteListContainer