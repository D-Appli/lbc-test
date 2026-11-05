package fr.leboncoin.feature.albums.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.leboncoin.core.domain.error.isOfflineError
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.core.domain.usecase.GetPagedAlbumsUseCase
import fr.leboncoin.core.domain.usecase.LoadAlbumsUseCase
import fr.leboncoin.core.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    getPagedAlbums: GetPagedAlbumsUseCase,
    private val loadAlbums: LoadAlbumsUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : ViewModel() {

    private var refreshJob: Job? = null

    private val _uiState = MutableStateFlow<AlbumsUiState>(AlbumsUiState.Idle)

    val uiState: StateFlow<AlbumsUiState> = _uiState.asStateFlow()

    /**
     * Cached albums, loaded page by page as the list scrolls. They are refreshed from the
     * network when the screen first collects them; `cachedIn` keeps the loaded pages in the
     * ViewModel, so a configuration change neither reloads them nor triggers a new refresh.
     */
    val albums: Flow<PagingData<Album>> = flow { emitAll(getPagedAlbums()) }
        .catch {
            // TODO: Track the failure. It is not reported anywhere else.
            _uiState.update { AlbumsUiState.Unavailable }
        }
        .onStart { refresh() }
        .cachedIn(viewModelScope)

    /** The cache is updated, which reloads [albums] with the new favorite. */
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

    fun refresh() {
        if (refreshJob?.isActive == true) return
        // Before launching, so that it cannot replace a failure of paging that happens first.
        _uiState.update { AlbumsUiState.Loading }
        refreshJob = viewModelScope.launch {
            try {
                loadAlbums()
                _uiState.update { if (it is AlbumsUiState.Loading) AlbumsUiState.Idle else it }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val state = if (e.isOfflineError()) AlbumsUiState.Idle else AlbumsUiState.Error
                _uiState.update { if (it is AlbumsUiState.Loading) state else it }
            }
        }
    }
}
