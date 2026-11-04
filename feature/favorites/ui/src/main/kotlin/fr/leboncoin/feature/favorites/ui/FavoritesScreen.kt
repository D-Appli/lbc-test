package fr.leboncoin.feature.favorites.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adevinta.spark.components.buttons.ButtonFilled
import com.adevinta.spark.components.progress.Spinner
import com.adevinta.spark.components.scaffold.Scaffold
import com.adevinta.spark.components.text.Text
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.feature.favorites.presentation.FavoritesUiState
import fr.leboncoin.feature.favorites.presentation.FavoritesViewModel
import fr.leboncoin.libs.albums.ui.AlbumItem
import kotlinx.collections.immutable.ImmutableList

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onItemSelected: (Album) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(modifier = modifier) { contentPadding ->
        when (val state = uiState) {
            FavoritesUiState.Loading -> CenteredContent(contentPadding) {
                Spinner()
            }

            FavoritesUiState.Error -> CenteredContent(contentPadding) {
                Text(text = stringResource(R.string.favorites_error))
                ButtonFilled(
                    onClick = viewModel::onRetryClick,
                    text = stringResource(R.string.favorites_retry),
                )
            }

            is FavoritesUiState.Success -> Crossfade(
                targetState = state.albums.isEmpty(),
                label = "favorites content",
            ) { isEmpty ->
                if (isEmpty) {
                    CenteredContent(contentPadding) {
                        Text(text = stringResource(R.string.favorites_empty))
                    }
                } else {
                    FavoritesList(
                        albums = state.albums,
                        contentPadding = contentPadding,
                        onItemSelected = onItemSelected,
                        onFavoriteClick = viewModel::onFavoriteClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoritesList(
    albums: ImmutableList<Album>,
    contentPadding: PaddingValues,
    onItemSelected: (Album) -> Unit,
    onFavoriteClick: (Album) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = contentPadding,
    ) {
        items(
            items = albums,
            key = { album -> album.id },
            contentType = { "album" },
        ) { album ->
            AlbumItem(
                album = album,
                onItemSelected = onItemSelected,
                onFavoriteClick = onFavoriteClick,
                modifier = Modifier.animateItem(
                    fadeOutSpec = tween(durationMillis = 250),
                    placementSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow,
                        visibilityThreshold = IntOffset.VisibilityThreshold,
                    ),
                ),
            )
        }
    }
}

@Composable
private fun CenteredContent(
    contentPadding: PaddingValues,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content,
    )
}
