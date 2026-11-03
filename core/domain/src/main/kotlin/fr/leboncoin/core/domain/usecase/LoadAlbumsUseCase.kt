package fr.leboncoin.core.domain.usecase

import fr.leboncoin.core.domain.repository.AlbumRepository
import javax.inject.Inject

/**
 * Loads the albums from the network into the cache, which updates [GetPagedAlbumsUseCase].
 * The cache is only written if the albums changed.
 *
 * @throws Exception if the albums cannot be loaded, e.g. when offline; the cache is kept.
 */
class LoadAlbumsUseCase @Inject constructor(
    private val repository: AlbumRepository,
) {

    suspend operator fun invoke() = repository.loadAlbums()
}
