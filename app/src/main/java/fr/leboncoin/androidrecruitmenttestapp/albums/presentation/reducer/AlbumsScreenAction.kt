package fr.leboncoin.androidrecruitmenttestapp.albums.presentation.reducer

import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.model.CallToActions
import fr.leboncoin.core.presentation.state.Action
import fr.leboncoin.data.network.model.AlbumDto

sealed class AlbumsScreenAction : Action {

    data object SetLoading : AlbumsScreenAction()

    data class SetContent(
        val albums: List<AlbumDto>,
    ) : AlbumsScreenAction()

    data class SetError(
        val callToActions: CallToActions,
    ) : AlbumsScreenAction()
}
