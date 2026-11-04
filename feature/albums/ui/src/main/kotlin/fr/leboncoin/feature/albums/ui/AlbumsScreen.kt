package fr.leboncoin.feature.albums.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.adevinta.spark.SparkTheme
import com.adevinta.spark.components.scaffold.Scaffold
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.feature.albums.presentation.AlbumsViewModel
import fr.leboncoin.libs.albums.ui.AlbumItem
import kotlinx.coroutines.flow.flowOf

@Composable
fun AlbumsScreen(
    viewModel: AlbumsViewModel,
    onItemSelected : (Album) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Collected with the lifecycle of the composition; the pages are cached in the ViewModel.
    val albums = viewModel.albums.collectAsLazyPagingItems()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    AlbumsContent(
        albums = albums,
        isLoading = isLoading,
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
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onItemSelected: (Album) -> Unit,
    onFavoriteClick: (Album) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(modifier = modifier) { contentPadding ->
        // The loader is shown while loading, on start as after a swipe to refresh.
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize(),
            state = pullToRefreshState,
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = pullToRefreshState,
                    isRefreshing = isLoading,
                    // Below the status bar, as the list is drawn edge to edge.
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = contentPadding.calculateTopPadding()),
                )
            },
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = contentPadding,
            ) {
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
        }
    }
}

@Preview
@Composable
private fun AlbumsPreview() {
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

    SparkTheme {
        AlbumsContent(
            albums = flowOf(PagingData.from(albums)).collectAsLazyPagingItems(),
            isLoading = false,
            onRefresh = {},
            onItemSelected = {},
            onFavoriteClick = {},
        )
    }
}
