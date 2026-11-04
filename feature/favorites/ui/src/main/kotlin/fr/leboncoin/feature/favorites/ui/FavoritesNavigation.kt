package fr.leboncoin.feature.favorites.ui

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import fr.leboncoin.core.domain.model.Album
import kotlinx.serialization.Serializable

@Serializable
data object FavoritesRoute : NavKey

fun EntryProviderScope<NavKey>.favoritesEntry(onItemSelected: (Album) -> Unit) {
    entry<FavoritesRoute> {
        FavoritesScreen(
            viewModel = hiltViewModel(),
            onItemSelected = onItemSelected,
        )
    }
}
