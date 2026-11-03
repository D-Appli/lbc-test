package fr.leboncoin.feature.favorites.ui

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object FavoritesRoute : NavKey

fun EntryProviderScope<NavKey>.favoritesEntry() {
    entry<FavoritesRoute> {
        FavoritesScreen(viewModel = hiltViewModel())
    }
}
