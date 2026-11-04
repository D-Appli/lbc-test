package fr.leboncoin.core.domain.usecase

import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.core.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Emits all the favorite albums of the cache, then again each time the cache changes, e.g. when
 * [ToggleFavoriteUseCase] adds or removes a favorite.
 */
class GetFavoriteAlbumsUseCase @Inject constructor(
    private val repository: AlbumRepository,
) {

    operator fun invoke(): Flow<List<Album>> = repository.getFavoriteAlbums()
}
