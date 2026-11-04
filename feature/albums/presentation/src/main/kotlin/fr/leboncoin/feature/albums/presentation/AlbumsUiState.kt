package fr.leboncoin.feature.albums.presentation

import javax.annotation.concurrent.Immutable

/** State of the load only: the albums themselves come from the paged flow of the ViewModel. */
@Immutable
sealed interface AlbumsUiState {

    data object Idle : AlbumsUiState

    data object Loading : AlbumsUiState

    data object Error : AlbumsUiState

    data object Unavailable : AlbumsUiState
}
