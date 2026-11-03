package fr.leboncoin.androidrecruitmenttestapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.adevinta.spark.components.scaffold.Scaffold
import fr.leboncoin.androidrecruitmenttestapp.AlbumsViewModel
import fr.leboncoin.core.domain.model.Album

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumsScreen(
    viewModel: AlbumsViewModel,
    onItemSelected : (Album) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Collected with the lifecycle of the composition; the pages are cached in the ViewModel.
    val albums = viewModel.albums.collectAsLazyPagingItems()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(modifier = modifier) { contentPadding ->
        // The loader is shown while loading, on start as after a swipe to refresh.
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxSize(),
            state = pullToRefreshState,
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = pullToRefreshState,
                    isRefreshing = isLoading,
                    // Below the status bar, as the list is drawn edge to edge.
                    modifier = Modifier
                        .align(Alignment.Center)
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
                    // Accessing an item close to the end of the loaded pages loads the next one.
                    albums[index]?.let { album ->
                        AlbumItem(
                            album = album,
                            onItemSelected = onItemSelected,
                        )
                    }
                }
            }
        }
    }
}
