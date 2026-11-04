package fr.leboncoin.feature.details.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adevinta.spark.SparkTheme
import com.adevinta.spark.components.buttons.ButtonShape
import com.adevinta.spark.components.iconbuttons.IconButtonContrast
import com.adevinta.spark.components.iconbuttons.IconButtonIntent
import com.adevinta.spark.components.iconbuttons.IconButtonSize
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

private val ImageCornerRadius = 28.dp
private val TopBarButtonPadding = 8.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    viewModel: DetailsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(modifier = modifier) {
        // The app bar is drawn over the content, so the image goes behind it.
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                DetailsUiState.Loading -> CenteredContent {
                    Spinner()
                }

                DetailsUiState.Error -> CenteredContent {
                    Text(text = stringResource(R.string.details_error))
                }

                is DetailsUiState.Success -> AlbumDetails(album = state.album)
            }

            // The Spark TopAppBar does not allow a transparent container.
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButtonContrast(
                        icon = SparkIcons.ArrowLeft,
                        onClick = onBack,
                        modifier = Modifier.padding(start = TopBarButtonPadding),
                        intent = IconButtonIntent.Neutral,
                        shape = ButtonShape.Pill,
                        size = IconButtonSize.Small,
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                ),
            )
        }
    }
}

@Composable
private fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
) {
    IconButtonContrast(
        icon = if (isFavorite) SparkIcons.LikeFill else SparkIcons.LikeOutline,
        onClick = onClick,
        modifier = Modifier.padding(end = TopBarButtonPadding),
        intent = if (isFavorite) IconButtonIntent.Danger else IconButtonIntent.Neutral,
        shape = ButtonShape.Pill,
        size = IconButtonSize.Small,
        contentDescription = stringResource(
            if (isFavorite) {
                R.string.details_remove_from_favorites
            } else {
                R.string.details_add_to_favorites
            },
        ),
    )
}

@Composable
private fun AlbumDetails(album: Album) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        AlbumImage(
            url = album.url,
            contentDescription = album.title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(bottomStart = ImageCornerRadius, bottomEnd = ImageCornerRadius)),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
private fun CenteredContent(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
