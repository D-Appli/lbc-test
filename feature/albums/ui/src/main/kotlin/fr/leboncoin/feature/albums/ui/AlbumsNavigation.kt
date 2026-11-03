package fr.leboncoin.feature.albums.ui

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import fr.leboncoin.core.domain.model.Album
import kotlinx.serialization.Serializable
import java.util.Map.entry

@Serializable
data object AlbumsRoute : NavKey

fun EntryProviderScope<NavKey>.albumsEntry(onItemSelected: (Album) -> Unit) {
    entry<AlbumsRoute> {
        AlbumsScreen(
            viewModel = hiltViewModel(),
            onItemSelected = onItemSelected,
        )
    }
}
