package fr.leboncoin.feature.favorites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.core.domain.usecase.GetFavoriteAlbumsUseCase
import fr.leboncoin.core.domain.usecase.ToggleFavoriteUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    getFavoriteAlbums: GetFavoriteAlbumsUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : ViewModel() {

    private val retries = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /** An error ends the observed favorites, so a retry has to observe them again. */
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<FavoritesUiState> = retries
        .onStart { emit(Unit) }
        .flatMapLatest {
            getFavoriteAlbums()
                .map<List<Album>, FavoritesUiState> { FavoritesUiState.Success(it.toImmutableList()) }
                .onStart { emit(FavoritesUiState.Loading) }
                .catch {
                    // TODO this should never happen, would be nice to track
                    emit(FavoritesUiState.Error)
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FavoritesUiState.Loading,
        )

    /** The cache is updated, which removes the album from [uiState]. */
    fun onFavoriteClick(album: Album) {
        viewModelScope.launch {
            try {
                toggleFavorite(album.id)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // TODO: Track the failure. The album simply keeps its current state.
            }
        }
    }

    fun onRetryClick() {
        retries.tryEmit(Unit)
    }
}
