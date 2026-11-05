package fr.leboncoin.core.data.repository

import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
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
import org.junit.Assert.assertNull
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
    fun `getPagedAlbums maps the cached entities to albums`() = runTest {
        val pagingSourceFactory = listOf(entity).asPagingSourceFactory()
        every { albumDao.pagingSource() } answers { pagingSourceFactory() }

        assertEquals(listOf(album), repository.getPagedAlbums().asSnapshot())
    }

    @Test
    fun `getPagedAlbums maps the favorites`() = runTest {
        val pagingSourceFactory = listOf(entity.copy(isFavorite = true)).asPagingSourceFactory()
        every { albumDao.pagingSource() } answers { pagingSourceFactory() }

        assertEquals(listOf(album.copy(isFavorite = true)), repository.getPagedAlbums().asSnapshot())
    }

    @Test
    fun `getFavoriteAlbums maps the cached favorites to albums`() = runTest {
        every { albumDao.observeFavorites() } returns flowOf(listOf(entity.copy(isFavorite = true)))

        assertEquals(listOf(album.copy(isFavorite = true)), repository.getFavoriteAlbums().first())
    }

    @Test
    fun `getAlbum maps the cached album`() = runTest {
        every { albumDao.observeAlbum(1) } returns flowOf(entity)

        assertEquals(album, repository.getAlbum(1).first())
    }

    @Test
    fun `getAlbum emits null when the album is not cached`() = runTest {
        every { albumDao.observeAlbum(1) } returns flowOf(null)

        assertNull(repository.getAlbum(1).first())
    }

    @Test
    fun `toggleFavorite toggles the album in the cache`() = runTest {
        coEvery { albumDao.toggleFavorite(any()) } returns Unit

        repository.toggleFavorite(1)

        coVerify(exactly = 1) { albumDao.toggleFavorite(1) }
    }

    @Test
    fun `loadAlbums writes the network albums to the cache`() = runTest {
        coEvery { apiService.getAlbums() } returns listOf(dto)
        coEvery { albumDao.replaceAllIfChanged(any()) } returns true

        repository.loadAlbums()

        coVerify(exactly = 1) { albumDao.replaceAllIfChanged(listOf(entity)) }
    }

    @Test
    fun `loadAlbums writes identical duplicates once`() = runTest {
        coEvery { apiService.getAlbums() } returns listOf(dto, dto)
        coEvery { albumDao.replaceAllIfChanged(any()) } returns true

        repository.loadAlbums()

        coVerify(exactly = 1) { albumDao.replaceAllIfChanged(listOf(entity)) }
    }

    @Test
    fun `loadAlbums keeps the first of conflicting duplicates`() = runTest {
        val other = AlbumDto(id = 2, albumId = 2, title = "o", url = "ou", thumbnailUrl = "otu")
        coEvery { apiService.getAlbums() } returns listOf(dto, other, dto.copy(title = "wrong"))
        coEvery { albumDao.replaceAllIfChanged(any()) } returns true

        repository.loadAlbums()

        coVerify(exactly = 1) {
            albumDao.replaceAllIfChanged(
                listOf(
                    entity,
                    AlbumEntity(id = 2, albumId = 2, title = "o", url = "ou", thumbnailUrl = "otu"),
                )
            )
        }
    }

    @Test
    fun `loadAlbums drops the albums with a missing field`() = runTest {
        coEvery { apiService.getAlbums() } returns listOf(
            dto,
            dto.copy(id = null),
            dto.copy(id = 2, albumId = null),
            dto.copy(id = 3, title = null),
            dto.copy(id = 4, url = null),
            dto.copy(id = 5, thumbnailUrl = null),
        )
        coEvery { albumDao.replaceAllIfChanged(any()) } returns true

        repository.loadAlbums()

        coVerify(exactly = 1) { albumDao.replaceAllIfChanged(listOf(entity)) }
    }

    @Test
    fun `loadAlbums keeps a valid album over an invalid duplicate`() = runTest {
        coEvery { apiService.getAlbums() } returns listOf(dto.copy(title = null), dto)
        coEvery { albumDao.replaceAllIfChanged(any()) } returns true

        repository.loadAlbums()

        coVerify(exactly = 1) { albumDao.replaceAllIfChanged(listOf(entity)) }
    }

    @Test(expected = IllegalStateException::class)
    fun `loadAlbums throws and keeps the cache when all the albums are invalid`() = runTest {
        coEvery { apiService.getAlbums() } returns listOf(dto.copy(id = null), dto.copy(title = null))

        try {
            repository.loadAlbums()
        } finally {
            coVerify(exactly = 0) { albumDao.replaceAllIfChanged(any()) }
        }
    }

    @Test
    fun `loadAlbums empties the cache when the response is empty`() = runTest {
        coEvery { apiService.getAlbums() } returns emptyList()
        coEvery { albumDao.replaceAllIfChanged(any()) } returns true

        repository.loadAlbums()

        coVerify(exactly = 1) { albumDao.replaceAllIfChanged(emptyList()) }
    }

    @Test(expected = IOException::class)
    fun `loadAlbums throws and keeps the cache when the network fails`() = runTest {
        coEvery { apiService.getAlbums() } throws IOException("network down")

        try {
            repository.loadAlbums()
        } finally {
            coVerify(exactly = 0) { albumDao.replaceAllIfChanged(any()) }
        }
    }
}
