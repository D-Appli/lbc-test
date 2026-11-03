package fr.leboncoin.androidrecruitmenttestapp.albums.presentation.viewmodel

import androidx.lifecycle.ViewModel
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.model.AlbumsScreenState
import kotlinx.coroutines.flow.StateFlow

abstract class AlbumsScreenViewModel : ViewModel() {
    abstract val stateFlow: StateFlow<AlbumsScreenState>
}
