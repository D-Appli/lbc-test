package fr.leboncoin.feature.details.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.core.domain.usecase.GetAlbumUseCase
import fr.leboncoin.core.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = DetailsViewModel.Factory::class)
class DetailsViewModel @AssistedInject constructor(
    @Assisted private val albumId: Int,
    getAlbum: GetAlbumUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : ViewModel() {

    val uiState: StateFlow<DetailsUiState> = getAlbum(albumId)
        .map<Album?, DetailsUiState> { album ->
            if (album != null) DetailsUiState.Success(album) else DetailsUiState.Error
        }
        .catch { emit(DetailsUiState.Error) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DetailsUiState.Loading,
        )

    /** The cache is updated, which updates the album of [uiState]. */
    fun onFavoriteClick() {
        viewModelScope.launch {
            try {
                toggleFavorite(albumId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // TODO: Track the failure. The album simply keeps its current state.
                Timber.e(e, "Failed to toggle the favorite of album $albumId")
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(albumId: Int): DetailsViewModel
    }
}
