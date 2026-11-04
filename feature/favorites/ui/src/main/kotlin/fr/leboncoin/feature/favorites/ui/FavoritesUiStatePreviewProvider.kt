package fr.leboncoin.feature.favorites.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.feature.favorites.presentation.FavoritesUiState
import kotlinx.collections.immutable.persistentListOf

internal class FavoritesUiStatePreviewProvider : PreviewParameterProvider<FavoritesUiState> {
    override val values: Sequence<FavoritesUiState> = sequenceOf(
        FavoritesUiState.Loading,
        FavoritesUiState.Error,
        FavoritesUiState.Success(persistentListOf()),
        FavoritesUiState.Success(
            persistentListOf(
                Album(
                    id = 1,
                    albumId = 1,
                    title = "accusamus beatae ad facilis",
                    url = "",
                    thumbnailUrl = "",
                    isFavorite = true,
                ),
                Album(
                    id = 2,
                    albumId = 1,
                    title = "reprehenderit est deserunt",
                    url = "",
                    thumbnailUrl = "",
                    isFavorite = true,
                ),
            ),
        ),
    )
}
