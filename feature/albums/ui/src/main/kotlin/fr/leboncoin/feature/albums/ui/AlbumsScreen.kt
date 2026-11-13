package fr.leboncoin.feature.albums.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.adevinta.spark.SparkTheme
import com.adevinta.spark.components.scaffold.Scaffold
import com.adevinta.spark.components.text.Text
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.feature.albums.presentation.AlbumsUiState
import fr.leboncoin.feature.albums.presentation.AlbumsViewModel
import fr.leboncoin.libs.albums.ui.AlbumItem
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
internal fun AlbumsScreen(
    viewModel: AlbumsViewModel,
    onItemSelected : (Album) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Collected with the lifecycle of the composition; the pages are cached in the ViewModel.
    val albums = viewModel.albums.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AlbumsContent(
        albums = albums,
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onItemSelected = onItemSelected,
        onFavoriteClick = viewModel::onFavoriteClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlbumsContent(
    albums: LazyPagingItems<Album>,
    uiState: AlbumsUiState,
    onRefresh: () -> Unit,
    onItemSelected: (Album) -> Unit,
    onFavoriteClick: (Album) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val isLoading = uiState is AlbumsUiState.Loading
    val isUnavailable = uiState is AlbumsUiState.Unavailable
    val isFailed = albums.loadState.refresh is LoadState.Error ||
        uiState is AlbumsUiState.Error || isUnavailable
    // Only without albums: the cached ones stay on screen, even if a later load fails.
    val message = when {
        albums.itemCount > 0 -> null
        isFailed -> R.string.albums_error
        isLoading -> R.string.albums_loading
        else -> R.string.albums_idle
    }

    Scaffold(
        modifier = modifier,
        // The default insets are only the system bars. The display cutout is added, so that in
        // landscape the albums are not drawn behind the camera. The bottom inset is already
        // consumed by the navigation bar of the app.
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { contentPadding ->
        // The loader is shown while loading, on start as after a swipe to refresh.
        // A Box, as PullToRefreshBox cannot disable the swipe.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullToRefresh(
                    isRefreshing = isLoading,
                    state = pullToRefreshState,
                    // A refresh cannot bring the albums back.
                    enabled = !isUnavailable,
                    onRefresh = onRefresh,
                ),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = contentPadding,
            ) {
                if (message != null) {
                    item(key = "message", contentType = "message") {
                        AlbumsMessage(
                            text = stringResource(message),
                            modifier = Modifier.fillParentMaxSize(),
                        )
                    }
                }
                items(
                    count = albums.itemCount,
                    key = albums.itemKey { album -> album.id },
                    contentType = albums.itemContentType { "album" },
                ) { index ->
                    // Accessing an item close to the loaded pages loads the next ones.
                    val album = albums[index]
                    if (album != null) {
                        AlbumItem(
                            album = album,
                            onItemSelected = onItemSelected,
                            onFavoriteClick = onFavoriteClick,
                        )
                    } else {
                        // Same height as AlbumItem, so that the scroll does not shift on load.
                        Spacer(Modifier.fillMaxWidth().height(120.dp))
                    }
                }
            }
            PullToRefreshDefaults.Indicator(
                state = pullToRefreshState,
                isRefreshing = isLoading,
                // Below the status bar, as the list is drawn edge to edge.
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = contentPadding.calculateTopPadding()),
            )
        }
    }
}

@Composable
private fun AlbumsMessage(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
internal fun AlbumsPreview() {
    val albums = listOf(
        Album(id = 1, albumId = 1, title = "accusamus beatae ad facilis", url = "", thumbnailUrl = ""),
        Album(
            id = 2,
            albumId = 1,
            title = "reprehenderit est deserunt",
            url = "",
            thumbnailUrl = "",
            isFavorite = true,
        ),
    )

    // A StateFlow gives the items on the first frame, which is the only one a static preview shows.
    val pagingData = remember { MutableStateFlow(PagingData.from(albums)) }

    SparkTheme {
        AlbumsContent(
            albums = pagingData.collectAsLazyPagingItems(),
            uiState = AlbumsUiState.Idle,
            onRefresh = {},
            onItemSelected = {},
            onFavoriteClick = {},
        )
    }
}

@Preview
@Composable
internal fun AlbumsMessagePreview(
    @PreviewParameter(AlbumsUiStatePreviewProvider::class) uiState: AlbumsUiState,
) {
    val pagingData = remember { MutableStateFlow(PagingData.empty<Album>()) }

    SparkTheme {
        AlbumsContent(
            albums = pagingData.collectAsLazyPagingItems(),
            uiState = uiState,
            onRefresh = {},
            onItemSelected = {},
            onFavoriteClick = {},
        )
    }
}
