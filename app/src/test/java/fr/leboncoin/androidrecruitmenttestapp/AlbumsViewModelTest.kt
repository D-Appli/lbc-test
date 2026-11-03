package fr.leboncoin.androidrecruitmenttestapp

import fr.leboncoin.core.data.network.api.AlbumApiService
import fr.leboncoin.core.data.network.model.AlbumDto
import fr.leboncoin.core.data.repository.AlbumRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class AlbumsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val albums = listOf(
        AlbumDto(id = 1, albumId = 1, title = "t", url = "u", thumbnailUrl = "tu")
    )

    @Test
    fun albums_notLoadedUntilCollected() = runTest {
        val apiService = mockk<AlbumApiService> {
            coEvery { getAlbums() } returns albums
        }

        AlbumsViewModel(AlbumRepository(apiService))

        coVerify(exactly = 0) { apiService.getAlbums() }
    }

    @Test
    fun albums_whenCollected_loadsAlbums() = runTest {
        val apiService = mockk<AlbumApiService> {
            coEvery { getAlbums() } returns albums
        }
        val vm = AlbumsViewModel(AlbumRepository(apiService))

        collect(vm)

        assertEquals(albums, vm.albums.value)
        coVerify(exactly = 1) { apiService.getAlbums() }
    }

    @Test
    fun albums_whenApiFails_keepsEmptyListWithoutCrashing() = runTest {
        val apiService = mockk<AlbumApiService> {
            coEvery { getAlbums() } throws IOException("network down")
        }
        val vm = AlbumsViewModel(AlbumRepository(apiService))

        collect(vm)

        assertEquals(emptyList<AlbumDto>(), vm.albums.value)
    }

    @Test
    fun refresh_afterFailure_reloadsAlbums() = runTest {
        val apiService = mockk<AlbumApiService> {
            coEvery { getAlbums() } throws IOException("network down") andThen albums
        }
        val vm = AlbumsViewModel(AlbumRepository(apiService))
        collect(vm)

        vm.refresh()

        assertEquals(albums, vm.albums.value)
        coVerify(exactly = 2) { apiService.getAlbums() }
    }

    /** `WhileSubscribed` only loads while someone collects, like the screen does. */
    private fun TestScope.collect(vm: AlbumsViewModel) {
        backgroundScope.launch(mainDispatcherRule.testDispatcher) { vm.albums.collect {} }
    }
}
