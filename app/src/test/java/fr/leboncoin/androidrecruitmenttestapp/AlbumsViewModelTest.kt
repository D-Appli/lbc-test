package fr.leboncoin.androidrecruitmenttestapp

import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.core.domain.repository.AlbumRepository
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
        Album(id = 1, albumId = 1, title = "t", url = "u", thumbnailUrl = "tu")
    )

    @Test
    fun albums_notLoadedUntilCollected() = runTest {
        val repository = mockk<AlbumRepository> {
            coEvery { getAllAlbums() } returns albums
        }

        AlbumsViewModel(repository)

        coVerify(exactly = 0) { repository.getAllAlbums() }
    }

    @Test
    fun albums_whenCollected_loadsAlbums() = runTest {
        val repository = mockk<AlbumRepository> {
            coEvery { getAllAlbums() } returns albums
        }
        val vm = AlbumsViewModel(repository)

        collect(vm)

        assertEquals(albums, vm.albums.value)
        coVerify(exactly = 1) { repository.getAllAlbums() }
    }

    @Test
    fun albums_whenRepositoryFails_keepsEmptyListWithoutCrashing() = runTest {
        val repository = mockk<AlbumRepository> {
            coEvery { getAllAlbums() } throws IOException("network down")
        }
        val vm = AlbumsViewModel(repository)

        collect(vm)

        assertEquals(emptyList<Album>(), vm.albums.value)
    }

    @Test
    fun refresh_afterFailure_reloadsAlbums() = runTest {
        val repository = mockk<AlbumRepository> {
            coEvery { getAllAlbums() } throws IOException("network down") andThen albums
        }
        val vm = AlbumsViewModel(repository)
        collect(vm)

        vm.refresh()

        assertEquals(albums, vm.albums.value)
        coVerify(exactly = 2) { repository.getAllAlbums() }
    }

    /** `WhileSubscribed` only loads while someone collects, like the screen does. */
    private fun TestScope.collect(vm: AlbumsViewModel) {
        backgroundScope.launch(mainDispatcherRule.testDispatcher) { vm.albums.collect {} }
    }
}
