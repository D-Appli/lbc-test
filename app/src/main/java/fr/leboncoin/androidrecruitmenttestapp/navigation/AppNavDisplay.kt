package fr.leboncoin.androidrecruitmenttestapp.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.adevinta.spark.ExperimentalSparkApi
import com.adevinta.spark.components.appbar.NavigationBar
import com.adevinta.spark.components.appbar.NavigationBarItem
import com.adevinta.spark.components.scaffold.Scaffold
import com.adevinta.spark.components.text.Text
import com.adevinta.spark.icons.LikeFill
import com.adevinta.spark.icons.SparkIcon
import com.adevinta.spark.icons.SparkIcons
import fr.leboncoin.androidrecruitmenttestapp.R
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.feature.albums.ui.AlbumsRoute
import fr.leboncoin.feature.albums.ui.albumsEntry
import fr.leboncoin.feature.favorites.ui.FavoritesRoute
import fr.leboncoin.feature.favorites.ui.favoritesEntry

private data class TopLevelDestination(
    val route: NavKey,
    val icon: SparkIcon,
    @param:StringRes val label: Int,
)

private val topLevelDestinations = listOf(
    TopLevelDestination(
        route = AlbumsRoute,
        icon = SparkIcon.DrawableRes(R.drawable.ic_music_note),
        label = R.string.navigation_albums,
    ),
    TopLevelDestination(
        route = FavoritesRoute,
        icon = SparkIcons.LikeFill,
        label = R.string.navigation_favorites,
    ),
)

@Composable
fun AppNavDisplay(
    onAlbumSelected: (Album) -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(AlbumsRoute)

    Scaffold(
        modifier = modifier,
        bottomBar = {
            AppNavigationBar(
                currentRoute = backStack.last(),
                onDestinationSelected = backStack::selectTopLevelRoute,
            )
        },
    ) { contentPadding ->
        // Only the bottom bar is applied: the screens draw edge to edge below the status bar.
        val bottomPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding())

        NavDisplay(
            backStack = backStack,
            modifier = Modifier
                .padding(bottomPadding)
                .consumeWindowInsets(bottomPadding),
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                // Scopes each ViewModel to its entry, so it is cleared when the entry is popped.
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
                albumsEntry(onItemSelected = onAlbumSelected)
                favoritesEntry(onItemSelected = onAlbumSelected)
            },
        )
    }
}

@OptIn(ExperimentalSparkApi::class)
@Composable
private fun AppNavigationBar(
    currentRoute: NavKey,
    onDestinationSelected: (NavKey) -> Unit,
) {
    NavigationBar {
        topLevelDestinations.forEach { destination ->
            NavigationBarItem(
                selected = destination.route == currentRoute,
                onClick = { onDestinationSelected(destination.route) },
                icon = destination.icon,
                label = { Text(stringResource(destination.label)) },
            )
        }
    }
}

/** Keeps [AlbumsRoute] at the bottom of the stack, so its state survives tab switches. */
internal fun NavBackStack<NavKey>.selectTopLevelRoute(route: NavKey) {
    if (last() == route) return
    while (size > 1) removeLastOrNull()
    if (route != AlbumsRoute) add(route)
}
