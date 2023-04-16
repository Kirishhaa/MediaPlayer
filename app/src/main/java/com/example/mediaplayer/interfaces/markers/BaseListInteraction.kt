package com.example.mediaplayer.interfaces.markers

import com.example.mediaplayer.interfaces.listcontainer.FavoriteListContainer
import com.example.mediaplayer.interfaces.listcontainer.ListContainer
import com.example.mediaplayer.interfaces.navigation.FragmentBackPressed

interface BaseListInteraction :
    FragmentBackPressed,
    ListContainer,
    AudioAdapterListener,
    FavoriteListContainer