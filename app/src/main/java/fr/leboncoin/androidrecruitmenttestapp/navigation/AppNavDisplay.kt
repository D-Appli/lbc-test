package fr.leboncoin.androidrecruitmenttestapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.feature.albums.ui.AlbumsRoute
import fr.leboncoin.feature.albums.ui.albumsEntry

@Composable
fun AppNavDisplay(
    onAlbumSelected: (Album) -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(AlbumsRoute)

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            // Scopes each ViewModel to its entry, so it is cleared when the entry is popped.
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            albumsEntry(onItemSelected = onAlbumSelected)
        },
    )
}
