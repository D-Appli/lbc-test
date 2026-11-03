package fr.leboncoin.androidrecruitmenttestapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.leboncoin.core.data.network.model.AlbumDto
import fr.leboncoin.core.data.repository.AlbumRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val repository: AlbumRepository,
) : ViewModel() {

    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    // TODO: Replace the plain list with a UI state (Loading / Success / Error), so the screen
    //  can show a progress indicator and an error with a retry, instead of an empty list.
    /**
     * Albums are loaded when the screen starts collecting, and kept for 5 seconds after it
     * stops, so a configuration change does not trigger a new request.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val albums: StateFlow<List<AlbumDto>> = refreshTrigger
        .onStart { emit(Unit) }
        .flatMapLatest {
            flow { emit(repository.getAllAlbums()) }
                // `catch` does not intercept cancellation, so it stays cooperative.
                .catch {
                    // TODO: Expose the failure as an Error UI state (see the TODO on albums).
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    /** Reloads the albums, e.g. for a retry. Keeps the current list if the request fails. */
    fun refresh() {
        refreshTrigger.tryEmit(Unit)
    }
}
