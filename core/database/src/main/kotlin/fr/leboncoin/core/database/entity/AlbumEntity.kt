package fr.leboncoin.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums")
data class AlbumEntity(
    @PrimaryKey val id: Int,
    val albumId: Int,
    val title: String,
    val url: String,
    val thumbnailUrl: String,
    /** Stored only on this device: the backend does not know about favorites. */
    val isFavorite: Boolean = false,
)
