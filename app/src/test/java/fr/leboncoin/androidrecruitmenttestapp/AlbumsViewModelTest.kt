package fr.leboncoin.androidrecruitmenttestapp

import app.cash.turbine.test
import fr.leboncoin.data.network.api.AlbumApiService
import fr.leboncoin.data.network.model.AlbumDto
import fr.leboncoin.data.repository.AlbumRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class AlbumsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadsAlbums_emitsNonEmptyList() = runTest {
        val albums = listOf(
            AlbumDto(id = 1, albumId = 1, title = "t", url = "u", thumbnailUrl = "tu")
        )
        val apiService = mockk<AlbumApiService> {
            coEvery { getAlbums() } returns albums
        }

        val vm = AlbumsViewModel(AlbumRepository(apiService))

        vm.albums.test {
            vm.loadAlbums()
            assertEquals(albums, awaitItem())
        }
        coVerify(exactly = 1) { apiService.getAlbums() }
    }

    @Test
    fun loadAlbums_whenApiFails_doesNotCrashNorEmit() = runTest {
        val apiService = mockk<AlbumApiService> {
            coEvery { getAlbums() } throws IOException("network down")
        }

        val vm = AlbumsViewModel(AlbumRepository(apiService))

        vm.albums.test {
            vm.loadAlbums()
            expectNoEvents()
        }
    }
}
