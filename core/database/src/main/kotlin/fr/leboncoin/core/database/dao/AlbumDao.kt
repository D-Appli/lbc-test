package fr.leboncoin.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import fr.leboncoin.core.database.entity.AlbumEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AlbumDao {

    /**
     * Loads the cached albums page by page. Room invalidates the source each time the table
     * changes, so the pages are reloaded with the new albums.
     */
    @Query("SELECT * FROM albums ORDER BY id")
    abstract fun pagingSource(): PagingSource<Int, AlbumEntity>

    /** Emits the favorite albums, then again each time the table changes. */
    @Query("SELECT * FROM albums WHERE isFavorite = 1 ORDER BY id")
    abstract fun observeFavorites(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM albums ORDER BY id")
    abstract suspend fun getAll(): List<AlbumEntity>

    // Ids are deduplicated by the caller; REPLACE is only a safety net.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertAll(albums: List<AlbumEntity>)

    @Query("DELETE FROM albums")
    protected abstract suspend fun deleteAll()

    @Query("SELECT id FROM albums WHERE isFavorite = 1")
    protected abstract suspend fun getFavoriteIds(): List<Int>

    @Query("UPDATE albums SET isFavorite = NOT isFavorite WHERE id = :id")
    abstract suspend fun toggleFavorite(id: Int)

    /**
     * Replaces the cached albums with [albums], only if they differ from the cache, so an
     * unchanged response does not write to the database or invalidate [pagingSource].
     * [albums] must have unique ids.
     *
     * Favorites only exist in the cache, so they are kept for the albums that are still in
     * [albums], whatever their `isFavorite` value. The favorites of the other albums are lost.
     *
     * @return true if the cache was updated.
     */
    @Transaction
    open suspend fun replaceAllIfChanged(albums: List<AlbumEntity>): Boolean {
        val favoriteIds = getFavoriteIds().toSet()
        val newAlbums = albums
            .map { it.copy(isFavorite = it.id in favoriteIds) }
            .sortedBy { it.id }
        if (getAll() == newAlbums) return false
        deleteAll()
        insertAll(newAlbums)
        return true
    }
}
