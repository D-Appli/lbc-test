package fr.leboncoin.feature.albums.ui

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.feature.albums.api.AlbumsRoute
import java.util.Map.entry

fun EntryProviderScope<NavKey>.albumsEntry(onItemSelected: (Album) -> Unit) {
    entry<AlbumsRoute> {
        AlbumsScreen(
            viewModel = hiltViewModel(),
            onItemSelected = onItemSelected,
        )
    }
}
