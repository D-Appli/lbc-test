package fr.leboncoin.core.domain.usecase

import androidx.paging.PagingData
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.core.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Pages through the cached albums, the single source of truth: they are loaded as the list
 * scrolls, and reloaded each time [LoadAlbumsUseCase] updates the cache.
 */
class GetPagedAlbumsUseCase @Inject constructor(
    private val repository: AlbumRepository,
) {

    operator fun invoke(): Flow<PagingData<Album>> = repository.getPagedAlbums()
}
