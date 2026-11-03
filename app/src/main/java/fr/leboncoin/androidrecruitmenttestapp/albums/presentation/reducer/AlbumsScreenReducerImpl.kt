package fr.leboncoin.androidrecruitmenttestapp.albums.presentation.reducer

import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.mapper.AlbumsScreenMapper
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.model.AlbumsScreenState
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.model.AlbumsScreenState.Content
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.reducer.AlbumsScreenAction.SetContent
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.reducer.AlbumsScreenAction.SetError
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.reducer.AlbumsScreenAction.SetLoading
import fr.leboncoin.core.kotlin.coroutines.providers.DispatcherProvider
import javax.inject.Inject

class AlbumsScreenReducerImpl @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val mapper: AlbumsScreenMapper,
) : AlbumsScreenReducer(
    initialState = AlbumsScreenState.None,
    dispatcherProvider = dispatcherProvider,
) {

    override fun reduce(
        action: AlbumsScreenAction,
        currentState: AlbumsScreenState,
    ): AlbumsScreenState {
        return when (action) {
            is SetLoading -> onSetLoading(currentState)
            is SetContent -> mapper.map(action.albums)
            is SetError -> onSetError(action, currentState)
        }
    }

    /** Keeps the displayed albums while they are reloaded. */
    private fun onSetLoading(currentState: AlbumsScreenState): AlbumsScreenState {
        val state = currentState as? Content ?: return AlbumsScreenState.Loading
        return state.copy(isLoading = true)
    }

    /** Keeps the displayed albums if a reload fails. */
    private fun onSetError(action: SetError, currentState: AlbumsScreenState): AlbumsScreenState {
        val state = currentState as? Content
            ?: return AlbumsScreenState.Error(onRetry = action.callToActions.onRetry)
        return state.copy(isLoading = false)
    }
}
