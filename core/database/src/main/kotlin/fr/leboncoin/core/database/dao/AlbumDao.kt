package fr.leboncoin.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import fr.leboncoin.core.database.entity.AlbumEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AlbumDao {

    /** Emits the cached albums, then again each time the table changes. */
    @Query("SELECT * FROM albums ORDER BY id")
    abstract fun observeAll(): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM albums ORDER BY id")
    protected abstract suspend fun getAll(): List<AlbumEntity>

    // Ids are deduplicated by the caller; REPLACE is only a safety net.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertAll(albums: List<AlbumEntity>)

    @Query("DELETE FROM albums")
    protected abstract suspend fun deleteAll()

    /**
     * Replaces the cached albums with [albums], only if they differ from the cache, so an
     * unchanged response does not write to the database or re-emit [observeAll].
     * [albums] must have unique ids.
     *
     * @return true if the cache was updated.
     */
    @Transaction
    open suspend fun replaceAllIfChanged(albums: List<AlbumEntity>): Boolean {
        val newAlbums = albums.sortedBy { it.id }
        if (getAll() == newAlbums) return false
        deleteAll()
        insertAll(newAlbums)
        return true
    }
}
