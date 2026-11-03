package fr.leboncoin.core.data.repository

import fr.leboncoin.core.data.mapper.toDomain
import fr.leboncoin.core.data.mapper.toEntity
import fr.leboncoin.core.data.network.api.AlbumApiService
import fr.leboncoin.core.database.dao.AlbumDao
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.core.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Offline-first: the database is the single source of truth, the network only updates it. */
internal class AlbumRepositoryImpl @Inject constructor(
    private val albumApiService: AlbumApiService,
    private val albumDao: AlbumDao,
) : AlbumRepository {

    override fun observeAlbums(): Flow<List<Album>> =
        albumDao.observeAll()
            // Room re-emits on any write to the table, even with the same rows.
            .distinctUntilChanged()
            .map { albums -> albums.map { it.toDomain() } }

    override suspend fun refreshAlbums() {
        val albums = albumApiService.getAlbums().map { it.toEntity() }
        albumDao.replaceAllIfChanged(albums)
    }
}
