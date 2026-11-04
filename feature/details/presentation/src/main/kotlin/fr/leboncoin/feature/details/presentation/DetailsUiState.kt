package fr.leboncoin.feature.details.presentation

import androidx.compose.runtime.Immutable
import fr.leboncoin.core.domain.model.Album

@Immutable
sealed interface DetailsUiState {

    data object Loading : DetailsUiState

    data class Success(val album: Album) : DetailsUiState

    /** The album cannot be read, or a refresh removed it from the cache. */
    data object Error : DetailsUiState
}
