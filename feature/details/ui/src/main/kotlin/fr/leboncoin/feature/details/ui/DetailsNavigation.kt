package fr.leboncoin.feature.details.ui

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import fr.leboncoin.feature.details.api.DetailsRoute
import fr.leboncoin.feature.details.presentation.DetailsViewModel

fun EntryProviderScope<NavKey>.detailsEntry(onBack: () -> Unit) {
    entry<DetailsRoute> { route ->
        DetailsScreen(
            viewModel = hiltViewModel<DetailsViewModel, DetailsViewModel.Factory>(
                creationCallback = { factory -> factory.create(route.albumId) },
            ),
            onBack = onBack,
        )
    }
}
