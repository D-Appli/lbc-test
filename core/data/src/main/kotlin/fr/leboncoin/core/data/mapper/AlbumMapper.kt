package fr.leboncoin.core.data.mapper

import fr.leboncoin.core.data.network.model.AlbumDto
import fr.leboncoin.core.domain.model.Album

internal fun AlbumDto.toDomain(): Album = Album(
    id = id,
    albumId = albumId,
    title = title,
    url = url,
    thumbnailUrl = thumbnailUrl,
)
