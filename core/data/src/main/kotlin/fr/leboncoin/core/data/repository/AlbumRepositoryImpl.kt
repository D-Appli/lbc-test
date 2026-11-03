package fr.leboncoin.core.data.repository

import fr.leboncoin.core.data.mapper.toDomain
import fr.leboncoin.core.data.network.api.AlbumApiService
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.core.domain.repository.AlbumRepository
import javax.inject.Inject

internal class AlbumRepositoryImpl @Inject constructor(
    private val albumApiService: AlbumApiService,
) : AlbumRepository {

    override suspend fun getAllAlbums(): List<Album> =
        albumApiService.getAlbums().map { it.toDomain() }
}
