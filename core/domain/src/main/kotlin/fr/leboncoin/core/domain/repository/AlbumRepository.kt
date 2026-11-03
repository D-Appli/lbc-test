package fr.leboncoin.core.domain.repository

import fr.leboncoin.core.domain.model.Album

interface AlbumRepository {

    suspend fun getAllAlbums(): List<Album>
}
