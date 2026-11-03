package fr.leboncoin.androidrecruitmenttestapp.albums.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.model.AlbumsScreenState
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.model.CallToActions
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.reducer.AlbumsScreenAction
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.reducer.AlbumsScreenReducer
import fr.leboncoin.core.kotlin.coroutines.providers.DispatcherProvider
import fr.leboncoin.data.repository.AlbumRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsScreenViewModelImpl @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val reducer: AlbumsScreenReducer,
    private val repository: AlbumRepository,
) : AlbumsScreenViewModel() {

    private var loadJob: Job? = null

    /**
     * Albums are loaded when the screen starts collecting, and kept for 5 seconds after it
     * stops, so a configuration change does not trigger a new request.
     */
    override val stateFlow: StateFlow<AlbumsScreenState> = reducer.stateFlow
        .onStart { loadContent() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = reducer.stateFlow.value,
        )

    private fun loadContent() {
        // A retry or a restarted collection must not run two requests in parallel.
        loadJob?.cancel()
        loadJob = viewModelScope.launch(dispatcherProvider.io) {
            try {
                reducer.update(AlbumsScreenAction.SetLoading)
                val albums = repository.getAllAlbums()
                reducer.update(AlbumsScreenAction.SetContent(albums))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                println("getAllAlbums error $e")
                reducer.update(AlbumsScreenAction.SetError(bindCallToAction()))
            }
        }
    }

    private fun bindCallToAction() = CallToActions(
        onRetry = ::loadContent,
    )

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
