package fr.leboncoin.feature.details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adevinta.spark.ExperimentalSparkApi
import com.adevinta.spark.SparkTheme
import com.adevinta.spark.components.appbar.TopAppBar
import com.adevinta.spark.components.iconbuttons.IconButtonGhost
import com.adevinta.spark.components.iconbuttons.IconButtonIntent
import com.adevinta.spark.components.progress.Spinner
import com.adevinta.spark.components.scaffold.Scaffold
import com.adevinta.spark.components.text.Text
import com.adevinta.spark.icons.ArrowLeft
import com.adevinta.spark.icons.LikeFill
import com.adevinta.spark.icons.LikeOutline
import com.adevinta.spark.icons.SparkIcons
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.feature.details.presentation.DetailsUiState
import fr.leboncoin.feature.details.presentation.DetailsViewModel
import fr.leboncoin.libs.albums.ui.AlbumImage
import fr.leboncoin.libs.albums.ui.AlbumLabel

private val SheetCornerRadius = 28.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSparkApi::class)
@Composable
fun DetailsScreen(
    viewModel: DetailsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButtonGhost(
                        icon = SparkIcons.ArrowLeft,
                        onClick = onBack,
                        intent = IconButtonIntent.Neutral,
                        contentDescription = stringResource(R.string.details_back),
                    )
                },
                actions = {
                    (uiState as? DetailsUiState.Success)?.let { state ->
                        FavoriteButton(
                            isFavorite = state.album.isFavorite,
                            onClick = viewModel::onFavoriteClick,
                        )
                    }
                },
            )
        },
    ) { contentPadding ->
        when (val state = uiState) {
            DetailsUiState.Loading -> CenteredContent(contentPadding) {
                Spinner()
            }

            DetailsUiState.Error -> CenteredContent(contentPadding) {
                Text(text = stringResource(R.string.details_error))
            }

            is DetailsUiState.Success -> AlbumDetails(
                album = state.album,
                contentPadding = contentPadding,
            )
        }
    }
}

@Composable
private fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
) {
    IconButtonGhost(
        icon = if (isFavorite) SparkIcons.LikeFill else SparkIcons.LikeOutline,
        onClick = onClick,
        intent = if (isFavorite) IconButtonIntent.Danger else IconButtonIntent.Neutral,
        contentDescription = stringResource(
            if (isFavorite) {
                R.string.details_remove_from_favorites
            } else {
                R.string.details_add_to_favorites
            },
        ),
    )
}

/** The rounded sheet overlaps the bottom of the image. */
@Composable
private fun AlbumDetails(
    album: Album,
    contentPadding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState()),
    ) {
        AlbumImage(
            url = album.url,
            contentDescription = album.title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
        )
        Column(
            modifier = Modifier
                .offset(y = -SheetCornerRadius)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = SheetCornerRadius, topEnd = SheetCornerRadius))
                .background(SparkTheme.colors.surface)
                .navigationBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = album.title,
                style = SparkTheme.typography.headline1,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AlbumLabel(text = stringResource(R.string.details_album_number, album.albumId))
                AlbumLabel(text = stringResource(R.string.details_track_number, album.id))
            }
        }
    }
}

@Composable
private fun CenteredContent(
    contentPadding: PaddingValues,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
