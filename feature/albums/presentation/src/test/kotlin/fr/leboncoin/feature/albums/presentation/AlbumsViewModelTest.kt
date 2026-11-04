package fr.leboncoin.feature.albums.presentation

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.core.domain.repository.AlbumRepository
import fr.leboncoin.core.domain.usecase.GetPagedAlbumsUseCase
import fr.leboncoin.core.domain.usecase.LoadAlbumsUseCase
import fr.leboncoin.core.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class AlbumsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val cached = listOf(album(1))
    private val remote = listOf(album(1), album(2))

    @Test
    fun `albums are not loaded from the network until collected`() = runTest {
        val repository = fakeRepository(cached) { remote }

        viewModel(repository)

        assertEquals(0, repository.loadCount)
    }

    @Test
    fun `albums show the network albums once loaded`() = runTest {
        val repository = fakeRepository(cached) { remote }
        val vm = viewModel(repository)

        assertEquals(remote, vm.albums.asSnapshot())
        assertEquals(1, repository.loadCount)
    }

    @Test
    fun `albums keep the cached albums when loading fails`() = runTest {
        val repository = fakeRepository(cached) { throw IOException("network down") }
        val vm = viewModel(repository)

        assertEquals(cached, vm.albums.asSnapshot())
    }

    @Test
    fun `albums load the first pages only`() = runTest {
        val many = (1..200).map(::album)
        val vm = viewModel(fakeRepository(many) { many })

        val loaded = vm.albums.asSnapshot()

        assertTrue("loaded ${loaded.size} albums", loaded.size < many.size)
        assertEquals(many.take(loaded.size), loaded)
    }

    @Test
    fun `albums load the next pages while scrolling`() = runTest {
        val many = (1..200).map(::album)
        val vm = viewModel(fakeRepository(many) { many })
        val firstPages = vm.albums.asSnapshot()

        val loaded = vm.albums.asSnapshot { scrollTo(index = firstPages.size + 30) }

        assertTrue("loaded ${loaded.size} albums", loaded.size > firstPages.size + 30)
        assertEquals(many.take(loaded.size), loaded)
    }

    @Test
    fun `refresh after a failure shows the network albums`() = runTest {
        var online = false
        val repository = fakeRepository(cached) {
            if (online) remote else throw IOException("network down")
        }
        val vm = viewModel(repository)
        assertEquals(cached, vm.albums.asSnapshot())

        online = true
        vm.refresh()

        assertEquals(remote, vm.albums.asSnapshot())
        assertEquals(2, repository.loadCount)
    }

    @Test
    fun `refresh is ignored while a load is in progress`() = runTest {
        val loaded = CompletableDeferred<Unit>()
        val repository = fakeRepository(cached) { loaded.await(); remote }
        val vm = viewModel(repository)

        vm.refresh()
        vm.refresh()
        loaded.complete(Unit)

        assertEquals(1, repository.loadCount)
    }

    @Test
    fun `isLoading is false before any load`() = runTest {
        val vm = viewModel(fakeRepository(cached) { remote })

        assertFalse(vm.isLoading.value)
    }

    @Test
    fun `isLoading is true while the albums load, then false`() = runTest {
        val loaded = CompletableDeferred<Unit>()
        val vm = viewModel(fakeRepository(cached) { loaded.await(); remote })

        vm.refresh()
        assertTrue(vm.isLoading.value)

        loaded.complete(Unit)
        assertFalse(vm.isLoading.value)
    }

    @Test
    fun `isLoading is false after a failed load`() = runTest {
        val vm = viewModel(fakeRepository(cached) { throw IOException("network down") })

        vm.refresh()

        assertFalse(vm.isLoading.value)
    }

    @Test
    fun `onFavoriteClick shows the album as a favorite`() = runTest {
        val vm = viewModel(fakeRepository(remote) { remote })
        vm.albums.asSnapshot()

        vm.onFavoriteClick(album(2))

        assertEquals(listOf(album(1), album(2).copy(isFavorite = true)), vm.albums.asSnapshot())
    }

    @Test
    fun `onFavoriteClick toggles a favorite off`() = runTest {
        val vm = viewModel(fakeRepository(remote) { remote })
        vm.albums.asSnapshot()

        vm.onFavoriteClick(album(2))
        vm.onFavoriteClick(album(2))

        assertEquals(remote, vm.albums.asSnapshot())
    }

    @Test
    fun `onFavoriteClick keeps the albums when toggling fails`() = runTest {
        val repository = fakeRepository(remote) { remote }
        val vm = viewModel(repository)
        vm.albums.asSnapshot()
        repository.toggleFailure = IOException("database error")

        vm.onFavoriteClick(album(2))

        assertEquals(remote, vm.albums.asSnapshot())
    }

    private fun viewModel(repository: AlbumRepository) = AlbumsViewModel(
        getPagedAlbums = GetPagedAlbumsUseCase(repository),
        loadAlbums = LoadAlbumsUseCase(repository),
        toggleFavorite = ToggleFavoriteUseCase(repository),
    )

    private fun TestScope.fakeRepository(
        cached: List<Album>,
        fetchRemote: suspend () -> List<Album>,
    ) = FakeAlbumRepository(backgroundScope, cached, fetchRemote)

    /** Pages through the cache like Room: a new list invalidates the paging source. */
    private class FakeAlbumRepository(
        scope: CoroutineScope,
        cached: List<Album>,
        private val fetchRemote: suspend () -> List<Album>,
    ) : AlbumRepository {

        private val cache = MutableStateFlow(cached)
        private val pagingSourceFactory = cache.asPagingSourceFactory(scope)
        var loadCount = 0
            private set
        var toggleFailure: Exception? = null

        override fun getPagedAlbums(): Flow<PagingData<Album>> =
            Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = false),
                pagingSourceFactory = pagingSourceFactory,
            ).flow

        override fun getFavoriteAlbums(): Flow<List<Album>> = emptyFlow()

        override fun getAlbum(id: Int): Flow<Album?> = emptyFlow()

        override suspend fun loadAlbums() {
            loadCount++
            cache.value = fetchRemote()
        }

        override suspend fun toggleFavorite(id: Int) {
            toggleFailure?.let { throw it }
            cache.value = cache.value.map { album ->
                if (album.id == id) album.copy(isFavorite = !album.isFavorite) else album
            }
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
