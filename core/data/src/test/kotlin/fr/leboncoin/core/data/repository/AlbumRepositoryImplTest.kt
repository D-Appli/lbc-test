package fr.leboncoin.core.data.repository

import fr.leboncoin.core.data.network.api.AlbumApiService
import fr.leboncoin.core.data.network.model.AlbumDto
import fr.leboncoin.core.database.dao.AlbumDao
import fr.leboncoin.core.database.entity.AlbumEntity
import fr.leboncoin.core.domain.model.Album
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException

class AlbumRepositoryImplTest {

    private val dto = AlbumDto(id = 1, albumId = 2, title = "t", url = "u", thumbnailUrl = "tu")
    private val entity = AlbumEntity(id = 1, albumId = 2, title = "t", url = "u", thumbnailUrl = "tu")
    private val album = Album(id = 1, albumId = 2, title = "t", url = "u", thumbnailUrl = "tu")

    private val apiService = mockk<AlbumApiService>()
    private val albumDao = mockk<AlbumDao>()
    private val repository = AlbumRepositoryImpl(apiService, albumDao)

    @Test
    fun observeAlbums_mapsEntitiesToDomainModels() = runTest {
        every { albumDao.observeAll() } returns flowOf(listOf(entity))

        assertEquals(listOf(album), repository.observeAlbums().first())
    }

    @Test
    fun refreshAlbums_writesRemoteAlbumsToTheCache() = runTest {
        coEvery { apiService.getAlbums() } returns listOf(dto)
        coEvery { albumDao.replaceAllIfChanged(any()) } returns true

        repository.refreshAlbums()

        coVerify(exactly = 1) { albumDao.replaceAllIfChanged(listOf(entity)) }
    }

    @Test
    fun refreshAlbums_withIdenticalDuplicates_writesOneAlbum() = runTest {
        coEvery { apiService.getAlbums() } returns listOf(dto, dto)
        coEvery { albumDao.replaceAllIfChanged(any()) } returns true

        repository.refreshAlbums()

        coVerify(exactly = 1) { albumDao.replaceAllIfChanged(listOf(entity)) }
    }

    @Test
    fun refreshAlbums_withConflictingDuplicates_keepsTheFirstOne() = runTest {
        val other = AlbumDto(id = 2, albumId = 2, title = "o", url = "ou", thumbnailUrl = "otu")
        coEvery { apiService.getAlbums() } returns listOf(dto, other, dto.copy(title = "wrong"))
        coEvery { albumDao.replaceAllIfChanged(any()) } returns true

        repository.refreshAlbums()

        coVerify(exactly = 1) {
            albumDao.replaceAllIfChanged(
                listOf(
                    entity,
                    AlbumEntity(id = 2, albumId = 2, title = "o", url = "ou", thumbnailUrl = "otu"),
                )
            )
        }
    }

    @Test(expected = IOException::class)
    fun refreshAlbums_whenApiFails_throwsAndKeepsTheCache() = runTest {
        coEvery { apiService.getAlbums() } throws IOException("network down")

        try {
            repository.refreshAlbums()
        } finally {
            coVerify(exactly = 0) { albumDao.replaceAllIfChanged(any()) }
        }
    }
}
