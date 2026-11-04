package fr.leboncoin.libs.albums.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import fr.leboncoin.core.domain.model.Album

internal class AlbumPreviewProvider : PreviewParameterProvider<Album> {
    private val album = Album(
        id = 1,
        albumId = 2,
        title = "accusamus beatae ad facilis cum similique qui sunt",
        url = "",
        thumbnailUrl = "",
    )

    override val values: Sequence<Album> = sequenceOf(
        album,
        album.copy(isFavorite = true),
    )
}
