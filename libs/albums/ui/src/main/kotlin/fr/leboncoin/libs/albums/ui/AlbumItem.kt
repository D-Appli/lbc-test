package fr.leboncoin.libs.albums.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.adevinta.spark.ExperimentalSparkApi
import com.adevinta.spark.SparkTheme
import com.adevinta.spark.components.card.Card
import com.adevinta.spark.components.chips.ChipTinted
import com.adevinta.spark.components.icons.Icon
import com.adevinta.spark.icons.LikeFill
import com.adevinta.spark.icons.SparkIcons
import com.adevinta.spark.tokens.dim3
import fr.leboncoin.core.domain.model.Album

@OptIn(ExperimentalSparkApi::class)
@Composable
fun AlbumItem(
    album: Album,
    onItemSelected : (Album) -> Unit,
    onFavoriteClick: (Album) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 16.dp),
        onClick = { onItemSelected(album) },
    ) {
        Row {
            AlbumImage(
                url = album.thumbnailUrl,
                contentDescription = album.title,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
            ) {
                Text(
                    text = album.title,
                    style = SparkTheme.typography.caption,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.weight(1f))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AlbumLabel(text = "Album #${album.albumId}")
                    ChipTinted(
                        onClick = { onFavoriteClick(album) },
                        leadingIcon = {
                            Icon(
                                sparkIcon = SparkIcons.LikeFill,
                                contentDescription = stringResource(
                                    if (album.isFavorite) {
                                        R.string.album_remove_from_favorites
                                    } else {
                                        R.string.album_add_to_favorites
                                    },
                                ),
                                modifier = Modifier.size(ChipIconSize),
                                tint = if (album.isFavorite) {
                                    SparkTheme.colors.error
                                } else {
                                    SparkTheme.colors.onSurface.dim3
                                },
                            )
                        },
                    ) {
                        Text(text = "Track #${album.id}")
                    }
                }
            }
        }
    }
}

// Same size as the leading icon of a Spark chip, which does not expose it.
private val ChipIconSize = 16.dp

@Preview
@Composable
private fun AlbumItemPreview(
    @PreviewParameter(AlbumPreviewProvider::class) album: Album,
) {
    SparkTheme {
        AlbumItem(album = album, onItemSelected = {}, onFavoriteClick = {})
    }
}
