package fr.leboncoin.feature.favorites.presentation

import androidx.compose.runtime.Immutable
import fr.leboncoin.core.domain.model.Album
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface FavoritesUiState {

    data object Loading : FavoritesUiState

    data class Success(val albums: ImmutableList<Album>) : FavoritesUiState

    data object Error : FavoritesUiState
}
