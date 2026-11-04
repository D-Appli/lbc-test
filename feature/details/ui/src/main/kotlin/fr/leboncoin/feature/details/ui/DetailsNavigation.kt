package fr.leboncoin.feature.details.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import fr.leboncoin.core.analytics.LocalAnalyticsHelper
import fr.leboncoin.feature.details.api.DetailsRoute
import fr.leboncoin.feature.details.presentation.DetailsViewModel

fun EntryProviderScope<NavKey>.detailsEntry(onBack: () -> Unit) {
    entry<DetailsRoute> { route ->
        TrackOpening(albumId = route.albumId)
        DetailsScreen(
            viewModel = hiltViewModel<DetailsViewModel, DetailsViewModel.Factory>(
                creationCallback = { factory -> factory.create(route.albumId) },
            ),
            onBack = onBack,
        )
    }
}

/** Saves that it was tracked, so that rotating the screen does not count as a new opening. */
@Composable
private fun TrackOpening(albumId: Int) {
    val analyticsHelper = LocalAnalyticsHelper.current
    var isTracked by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!isTracked) {
            isTracked = true
            analyticsHelper.trackSelection(albumId.toString())
            analyticsHelper.trackScreenView("Details")
        }
    }
}
