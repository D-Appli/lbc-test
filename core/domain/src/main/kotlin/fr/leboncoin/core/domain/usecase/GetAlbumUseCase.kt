package fr.leboncoin.core.domain.usecase

import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.core.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Emits the album of the cache, or null if it is not there, then again each time the cache
 * changes, e.g. when [ToggleFavoriteUseCase] adds it to the favorites.
 */
class GetAlbumUseCase @Inject constructor(
    private val repository: AlbumRepository,
) {

    operator fun invoke(id: Int): Flow<Album?> = repository.getAlbum(id)
}
