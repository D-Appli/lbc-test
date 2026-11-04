package fr.leboncoin.core.domain.usecase

import fr.leboncoin.core.domain.model.Album
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetFavoriteAlbumsUseCaseTest {

    @Test
    fun `emits the favorite albums of the repository`() = runTest {
        val albums = listOf(
            Album(id = 1, albumId = 2, title = "t", url = "u", thumbnailUrl = "tu", isFavorite = true),
        )
        val repository = FakeAlbumRepository(favoriteAlbums = flowOf(albums))

        assertEquals(albums, GetFavoriteAlbumsUseCase(repository)().first())
    }
}
