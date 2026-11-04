package fr.leboncoin.core.domain.usecase

import fr.leboncoin.core.domain.repository.AlbumRepository
import javax.inject.Inject

/**
 * Adds an album to the favorites, or removes it. Favorites are stored in the cache only, which
 * updates [GetPagedAlbumsUseCase].
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val repository: AlbumRepository,
) {

    suspend operator fun invoke(id: Int) = repository.toggleFavorite(id)
}
