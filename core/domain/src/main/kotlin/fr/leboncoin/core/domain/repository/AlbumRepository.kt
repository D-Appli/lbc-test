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

    /** Emits all the favorite albums, then again each time the cache changes. */
    fun getFavoriteAlbums(): Flow<List<Album>>

    /** Emits the album with this [id][Album.id], or null, then again when the cache changes. */
    fun getAlbum(id: Int): Flow<Album?>

    /**
     * Fetches the albums from the network and updates the cache if they changed. Favorites
     * are kept, except for the albums that the network no longer returns.
     *
     * @throws Exception if the albums cannot be fetched, e.g. when offline.
     */
    suspend fun loadAlbums()

    /** Adds the album with this [id][Album.id] to the favorites, or removes it. */
    suspend fun toggleFavorite(id: Int)
}
