package fr.leboncoin.core.domain.repository

import fr.leboncoin.core.domain.model.Album
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {

    /** Emits the cached albums, then again each time the cache changes. */
    fun observeAlbums(): Flow<List<Album>>

    /**
     * Fetches the albums from the network and updates the cache if they changed.
     *
     * @throws Exception if the albums cannot be fetched, e.g. when offline.
     */
    suspend fun refreshAlbums()
}
