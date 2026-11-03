package fr.leboncoin.androidrecruitmenttestapp.albums.presentation.model

import fr.leboncoin.core.presentation.state.State
import kotlinx.collections.immutable.ImmutableList

sealed class AlbumsScreenState : State {

    data object None : AlbumsScreenState()

    data object Loading : AlbumsScreenState()

    data class Error(
        val onRetry: () -> Unit,
    ) : AlbumsScreenState()

    /**
     * @param isLoading True while the albums are reloaded on top of the displayed list.
     */
    data class Content(
        val albums: ImmutableList<AlbumUiModel>,
        val isLoading: Boolean = false,
    ) : AlbumsScreenState()
}
