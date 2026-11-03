package fr.leboncoin.androidrecruitmenttestapp

import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.core.domain.repository.AlbumRepository
import fr.leboncoin.core.domain.usecase.ObserveAlbumsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val cached = listOf(album(1))
    private val remote = listOf(album(1), album(2))

    @Test
    fun albums_notRefreshedUntilCollected() = runTest {
        val repository = FakeAlbumRepository(cached) { remote }

        AlbumsViewModel(ObserveAlbumsUseCase(repository))

        assertEquals(0, repository.refreshCount)
    }

    @Test
    fun albums_whenCollected_showsRefreshedAlbums() = runTest {
        val repository = FakeAlbumRepository(cached) { remote }
        val vm = AlbumsViewModel(ObserveAlbumsUseCase(repository))

        collect(vm)

        assertEquals(remote, vm.albums.value)
        assertEquals(1, repository.refreshCount)
    }

    @Test
    fun albums_whenRefreshFails_showsCachedAlbums() = runTest {
        val repository = FakeAlbumRepository(cached) { throw IOException("network down") }
        val vm = AlbumsViewModel(ObserveAlbumsUseCase(repository))

        collect(vm)

        assertEquals(cached, vm.albums.value)
    }

    @Test
    fun refresh_afterFailure_showsRefreshedAlbums() = runTest {
        var online = false
        val repository = FakeAlbumRepository(cached) {
            if (online) remote else throw IOException("network down")
        }
        val vm = AlbumsViewModel(ObserveAlbumsUseCase(repository))
        collect(vm)

        online = true
        vm.refresh()

        assertEquals(remote, vm.albums.value)
        assertEquals(2, repository.refreshCount)
    }

    /** `WhileSubscribed` only loads while someone collects, like the screen does. */
    private fun TestScope.collect(vm: AlbumsViewModel) {
        backgroundScope.launch(mainDispatcherRule.testDispatcher) { vm.albums.collect {} }
    }

    private class FakeAlbumRepository(
        cached: List<Album>,
        private val fetchRemote: () -> List<Album>,
    ) : AlbumRepository {

        private val cache = MutableStateFlow(cached)
        var refreshCount = 0
            private set

        override fun observeAlbums(): Flow<List<Album>> = cache

        override suspend fun refreshAlbums() {
            refreshCount++
            cache.value = fetchRemote()
        }
    }

    private fun album(id: Int) = Album(
        id = id,
        albumId = 1,
        title = "title $id",
        url = "url $id",
        thumbnailUrl = "thumbnail $id",
    )
}
