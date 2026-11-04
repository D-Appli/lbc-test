package fr.leboncoin.core.domain.usecase

import fr.leboncoin.core.domain.model.Album
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetAlbumUseCaseTest {

    private val album = Album(id = 1, albumId = 2, title = "t", url = "u", thumbnailUrl = "tu")
    private val repository = FakeAlbumRepository(
        albumById = { id -> flowOf(album.takeIf { it.id == id }) },
    )

    @Test
    fun `emits the album of the repository with this id`() = runTest {
        assertEquals(album, GetAlbumUseCase(repository)(1).first())
    }

    @Test
    fun `emits null when the repository does not have the album`() = runTest {
        assertNull(GetAlbumUseCase(repository)(2).first())
    }
}
