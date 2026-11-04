package fr.leboncoin.feature.albums.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import fr.leboncoin.feature.albums.presentation.AlbumsUiState

internal class AlbumsUiStatePreviewProvider : PreviewParameterProvider<AlbumsUiState> {
    override val values: Sequence<AlbumsUiState> = sequenceOf(
        AlbumsUiState.Idle,
        AlbumsUiState.Loading,
        AlbumsUiState.Error,
    )
}
