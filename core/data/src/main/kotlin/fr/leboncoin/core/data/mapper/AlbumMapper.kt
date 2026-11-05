package fr.leboncoin.core.data.mapper

import fr.leboncoin.core.data.network.model.AlbumDto
import fr.leboncoin.core.database.entity.AlbumEntity
import fr.leboncoin.core.domain.model.Album

/** Returns null if a field is missing, as an album cannot be displayed without all of them. */
internal fun AlbumDto.toEntityOrNull(): AlbumEntity? = AlbumEntity(
    id = id ?: return null,
    albumId = albumId ?: return null,
    title = title ?: return null,
    url = url ?: return null,
    thumbnailUrl = thumbnailUrl ?: return null,
)

internal fun AlbumEntity.toDomain(): Album = Album(
    id = id,
    albumId = albumId,
    title = title,
    url = url,
    thumbnailUrl = thumbnailUrl,
    isFavorite = isFavorite,
)
