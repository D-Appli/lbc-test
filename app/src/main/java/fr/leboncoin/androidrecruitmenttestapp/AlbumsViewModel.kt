package fr.leboncoin.androidrecruitmenttestapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.core.domain.usecase.ObserveAlbumsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val observeAlbums: ObserveAlbumsUseCase,
) : ViewModel() {

    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    // TODO: Replace the plain list with a UI state (Loading / Success / Error) built from
    //  AlbumsResult.refreshStatus, so the screen can show a progress indicator and an error
    //  with a retry while still showing the cached albums.
    /**
     * Cached albums are shown as soon as the screen starts collecting, then refreshed from
     * the network. They are kept for 5 seconds after the screen stops collecting, so a
     * configuration change does not trigger a new refresh.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val albums: StateFlow<List<Album>> = refreshTrigger
        .onStart { emit(Unit) }
        .flatMapLatest {
            observeAlbums()
                .map { it.albums }
                // Network failures are reported in refreshStatus; this only catches cache
                // errors. `catch` does not intercept cancellation, so it stays cooperative.
                .catch {
                    // TODO: Expose the failure as an Error UI state (see the TODO on albums).
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    /** Refreshes the albums, e.g. for a retry. Keeps the cached albums if the refresh fails. */
    fun refresh() {
        refreshTrigger.tryEmit(Unit)
    }
}
