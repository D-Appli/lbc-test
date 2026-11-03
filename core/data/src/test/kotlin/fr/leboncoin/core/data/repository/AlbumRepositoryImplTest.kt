package fr.leboncoin.core.data.repository

import fr.leboncoin.core.data.network.api.AlbumApiService
import fr.leboncoin.core.data.network.model.AlbumDto
import fr.leboncoin.core.domain.model.Album
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AlbumRepositoryImplTest {

    @Test
    fun getAllAlbums_mapsDtosToDomainModels() = runTest {
        val apiService = mockk<AlbumApiService> {
            coEvery { getAlbums() } returns listOf(
                AlbumDto(id = 1, albumId = 2, title = "t", url = "u", thumbnailUrl = "tu")
            )
        }

        val albums = AlbumRepositoryImpl(apiService).getAllAlbums()

        assertEquals(
            listOf(Album(id = 1, albumId = 2, title = "t", url = "u", thumbnailUrl = "tu")),
            albums,
        )
    }
}
