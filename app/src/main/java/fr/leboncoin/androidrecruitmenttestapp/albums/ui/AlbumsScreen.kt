package fr.leboncoin.androidrecruitmenttestapp.albums.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adevinta.spark.components.buttons.ButtonFilled
import com.adevinta.spark.components.progress.LinearProgressIndicatorIndeterminate
import com.adevinta.spark.components.progress.Spinner
import com.adevinta.spark.components.scaffold.Scaffold
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.model.AlbumUiModel
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.model.AlbumsScreenState
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.viewmodel.AlbumsScreenViewModel
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.viewmodel.AlbumsScreenViewModelImpl

@Composable
fun AlbumsScreen(
    onItemSelected: (AlbumUiModel) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlbumsScreenViewModel = hiltViewModel<AlbumsScreenViewModelImpl>(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    Scaffold(modifier = modifier) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center,
        ) {
            when (val currentState = state) {
                is AlbumsScreenState.Content -> AlbumsContent(
                    state = currentState,
                    onItemSelected = onItemSelected,
                )
                is AlbumsScreenState.Error -> AlbumsError(currentState)
                AlbumsScreenState.Loading -> Spinner()
                AlbumsScreenState.None -> Unit
            }
        }
    }
}

@Composable
private fun AlbumsContent(
    state: AlbumsScreenState.Content,
    onItemSelected: (AlbumUiModel) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            items(
                items = state.albums,
                key = { album -> album.id },
            ) { album ->
                AlbumItem(
                    album = album,
                    onItemSelected = onItemSelected,
                )
            }
        }
        if (state.isLoading) {
            LinearProgressIndicatorIndeterminate(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
            )
        }
    }
}

@Composable
private fun AlbumsError(state: AlbumsScreenState.Error) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = "Albums could not be loaded.")
        ButtonFilled(
            onClick = state.onRetry,
            text = "Retry",
        )
    }
}
