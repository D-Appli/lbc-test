package fr.leboncoin.core.domain.usecase

import androidx.paging.PagingData
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.core.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

internal class FakeAlbumRepository(
    private val pagedAlbums: Flow<PagingData<Album>> = emptyFlow(),
    private val onLoad: suspend () -> Unit = {},
) : AlbumRepository {

    var loadCount = 0
        private set

    override fun getPagedAlbums(): Flow<PagingData<Album>> = pagedAlbums

    override suspend fun loadAlbums() {
        loadCount++
        onLoad()
    }
}
