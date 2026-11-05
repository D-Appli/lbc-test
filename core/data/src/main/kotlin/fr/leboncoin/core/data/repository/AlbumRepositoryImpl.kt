package fr.leboncoin.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import fr.leboncoin.core.data.mapper.toDomain
import fr.leboncoin.core.data.mapper.toEntityOrNull
import fr.leboncoin.core.data.network.api.AlbumApiService
import fr.leboncoin.core.database.dao.AlbumDao
import fr.leboncoin.core.database.entity.AlbumEntity
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.core.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Offline-first: the database is the single source of truth, the network only updates it. */
internal class AlbumRepositoryImpl @Inject constructor(
    private val albumApiService: AlbumApiService,
    private val albumDao: AlbumDao,
) : AlbumRepository {

    override fun getPagedAlbums(): Flow<PagingData<Album>> =
        Pager(
            // Placeholders keep the index of each album when the cache changes, so that
            // toggling a favorite does not make the list jump.
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = true),
            pagingSourceFactory = albumDao::pagingSource,
        ).flow.map { albums -> albums.map { it.toDomain() } }

    override fun getFavoriteAlbums(): Flow<List<Album>> =
        albumDao.observeFavorites().map { albums -> albums.map { it.toDomain() } }

    override fun getAlbum(id: Int): Flow<Album?> =
        albumDao.observeAlbum(id).map { album -> album?.toDomain() }

    override suspend fun loadAlbums() {
        val response = albumApiService.getAlbums()
        val albums = response.mapNotNull { it.toEntityOrNull() }
        // Replacing the cache with nothing would also lose the favorites.
        check(albums.isNotEmpty() || response.isEmpty()) { "All the albums of the response are invalid" }
        if (albums.size < response.size) {
            // TODO: Track the anomaly (e.g. a non-fatal report) so the backend can fix it.
            println("AlbumRepository: dropped ${response.size - albums.size} invalid albums")
        }
        albumDao.replaceAllIfChanged(albums.distinctById())
    }

    override suspend fun toggleFavorite(id: Int) = albumDao.toggleFavorite(id)

    /**
     * Ids are unique, but the backend may send one twice by mistake. Keeps the first
     * occurrence, so the result does not depend on anything but the response order, and
     * reports the ids whose duplicates have a different content.
     */
    private fun List<AlbumEntity>.distinctById(): List<AlbumEntity> {
        val conflictingIds = groupBy { it.id }
            .filterValues { duplicates -> duplicates.distinct().size > 1 }
            .keys
        if (conflictingIds.isNotEmpty()) {
            // TODO: Track the anomaly (e.g. a non-fatal report) so the backend can fix it.
            println("AlbumRepository: conflicting albums for ids $conflictingIds, keeping the first ones")
        }
        return distinctBy { it.id }
    }

    private companion object {
        // The API sends all the albums at once; only the cache is read page by page.
        const val PAGE_SIZE = 20
    }
}
