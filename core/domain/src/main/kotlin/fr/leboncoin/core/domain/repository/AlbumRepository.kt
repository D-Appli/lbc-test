package fr.leboncoin.core.domain.repository

import androidx.paging.PagingData
import fr.leboncoin.core.domain.model.Album
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {

    /**
     * Pages through the cached albums: they are loaded as the list scrolls, and reloaded
     * each time the cache changes.
     */
    fun getPagedAlbums(): Flow<PagingData<Album>>

    /**
     * Fetches the albums from the network and updates the cache if they changed.
     *
     * @throws Exception if the albums cannot be fetched, e.g. when offline.
     */
    suspend fun loadAlbums()
}
