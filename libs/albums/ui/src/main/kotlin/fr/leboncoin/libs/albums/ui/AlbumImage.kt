package fr.leboncoin.libs.albums.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.adevinta.spark.SparkTheme
import com.adevinta.spark.components.icons.Icon
import com.adevinta.spark.icons.NoPhoto
import com.adevinta.spark.icons.SparkIcons

/** Uses the image loader of the app, which sets the caches, the crossfade and the headers. */
@Composable
fun AlbumImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    var isFailed by remember(url) { mutableStateOf(false) }

    Box(modifier = modifier) {
        AsyncImage(
            model = url,
            contentDescription = contentDescription,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            onSuccess = { isFailed = false },
            onError = { isFailed = true },
        )
        if (isFailed) {
            AlbumImagePlaceholder(modifier = Modifier.matchParentSize())
        }
    }
}

@Composable
private fun AlbumImagePlaceholder(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.background(SparkTheme.colors.basicContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            sparkIcon = SparkIcons.NoPhoto,
            // The image above already describes the album.
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = SparkTheme.colors.onBasicContainer,
        )
    }
}

@Preview
@Composable
internal fun AlbumImagePlaceholderPreview() {
    SparkTheme {
        AlbumImagePlaceholder(modifier = Modifier.size(120.dp))
    }
}
