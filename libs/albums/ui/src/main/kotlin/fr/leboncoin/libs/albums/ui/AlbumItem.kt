package fr.leboncoin.libs.albums.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
            .padding(horizontal = 16.dp),
        onClick = { onItemSelected(album) },
    ) {
        // The card grows when the text needs it, e.g. with a large font.
        Row(verticalAlignment = Alignment.CenterVertically) {
            AlbumImage(
                url = album.thumbnailUrl,
                contentDescription = album.title,
                modifier = Modifier.size(CardMinHeight),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = CardMinHeight)
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = album.title,
                    modifier = Modifier.padding(bottom = 8.dp),
                    style = SparkTheme.typography.caption,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    itemVerticalAlignment = Alignment.CenterVertically,
                ) {
                    AlbumLabel(text = "Album #${album.albumId}")
                    ChipTinted(
                        onClick = { onFavoriteClick(album) },
                        leadingIcon = {
                            AlbumFavoriteIcon(
                                isFavorite = album.isFavorite,
                                contentDescription = stringResource(
                                    if (album.isFavorite) {
                                        R.string.album_remove_from_favorites
                                    } else {
                                        R.string.album_add_to_favorites
                                    },
                                ),
                                modifier = Modifier.size(ChipIconSize),
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
private val CardMinHeight = 120.dp

@Preview
@Composable
internal fun AlbumItemPreview(
    @PreviewParameter(AlbumPreviewProvider::class) album: Album,
) {
    SparkTheme {
        AlbumItem(album = album, onItemSelected = {}, onFavoriteClick = {})
    }
}
