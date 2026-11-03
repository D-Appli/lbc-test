package fr.leboncoin.androidrecruitmenttestapp.albums.presentation.reducer

import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.model.AlbumsScreenState
import fr.leboncoin.core.kotlin.coroutines.providers.DispatcherProvider
import fr.leboncoin.core.presentation.state.StateReducer

abstract class AlbumsScreenReducer(
    initialState: AlbumsScreenState,
    dispatcherProvider: DispatcherProvider,
) : StateReducer<AlbumsScreenState, AlbumsScreenAction>(initialState, dispatcherProvider)
