package fr.leboncoin.core.data.mapper

import fr.leboncoin.core.data.network.model.AlbumDto
import fr.leboncoin.core.database.entity.AlbumEntity
import fr.leboncoin.core.domain.model.Album

internal fun AlbumDto.toEntity(): AlbumEntity = AlbumEntity(
    id = id,
    albumId = albumId,
    title = title,
    url = url,
    thumbnailUrl = thumbnailUrl,
)

internal fun AlbumEntity.toDomain(): Album = Album(
    id = id,
    albumId = albumId,
    title = title,
    url = url,
    thumbnailUrl = thumbnailUrl,
    isFavorite = isFavorite,
)
