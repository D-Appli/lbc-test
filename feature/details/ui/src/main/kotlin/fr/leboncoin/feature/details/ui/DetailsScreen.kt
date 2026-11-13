package fr.leboncoin.feature.details.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
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

private val ImageCornerRadius = 36.dp
private val TopBarButtonPadding = 8.dp
private val TopGradientHeight = 120.dp
private const val WideScreenImageHeightRatio = 0.45f
private val TopGradientColors = listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)

@Composable
internal fun DetailsScreen(
    viewModel: DetailsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DetailsContent(
        uiState = uiState,
        onBack = onBack,
        onFavoriteClick = viewModel::onFavoriteClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsContent(
    uiState: DetailsUiState,
    onBack: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) {
        // The app bar is drawn over the content, so the image goes behind it.
        Box(modifier = Modifier.fillMaxSize()) {
            when (uiState) {
                DetailsUiState.Loading -> CenteredContent {
                    Spinner()
                }

                DetailsUiState.Error -> CenteredContent {
                    Text(text = stringResource(R.string.details_error))
                }

                is DetailsUiState.Success -> AlbumDetails(album = uiState.album)
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
                            onClick = onFavoriteClick,
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
    LightStatusBarIcons()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // On a wide screen, the image is cropped so that the text remains visible.
        val imageSize = if (maxWidth > maxHeight) {
            Modifier.height(maxHeight * WideScreenImageHeightRatio)
        } else {
            Modifier.aspectRatio(1f)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(imageSize)
                    .clip(RoundedCornerShape(bottomStart = ImageCornerRadius, bottomEnd = ImageCornerRadius)),
            ) {
                AlbumImage(
                    url = album.url,
                    contentDescription = album.title,
                    modifier = Modifier.matchParentSize(),
                )
                // Keeps the status bar icons readable, whatever the colors of the image.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(TopGradientHeight)
                        .background(Brush.verticalGradient(TopGradientColors)),
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom),
                    )
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
}

/** Only for the content drawn behind the status bar: it restores the icons when it leaves. */
@Composable
private fun LightStatusBarIcons() {
    val window = LocalActivity.current?.window ?: return
    val view = LocalView.current

    DisposableEffect(window, view) {
        val controller = WindowCompat.getInsetsController(window, view)
        val wereDark = controller.isAppearanceLightStatusBars
        controller.isAppearanceLightStatusBars = false
        onDispose { controller.isAppearanceLightStatusBars = wereDark }
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

@Preview(name = "Portrait", widthDp = 411, heightDp = 914)
@Preview(name = "Landscape", widthDp = 914, heightDp = 411)
@Composable
internal fun DetailsPreview(
    @PreviewParameter(DetailsUiStatePreviewProvider::class) uiState: DetailsUiState,
) {
    SparkTheme {
        DetailsContent(
            uiState = uiState,
            onBack = {},
            onFavoriteClick = {},
        )
    }
}
