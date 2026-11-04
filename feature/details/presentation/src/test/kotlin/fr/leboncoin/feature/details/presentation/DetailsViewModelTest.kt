package fr.leboncoin.feature.details.presentation

import androidx.paging.PagingData
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.core.domain.repository.AlbumRepository
import fr.leboncoin.core.domain.usecase.GetAlbumUseCase
import fr.leboncoin.core.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val cachedAlbum = MutableSharedFlow<Album?>()
    private val album = Album(id = 3, albumId = 1, title = "t", url = "u", thumbnailUrl = "tu")

    @Test
    fun `uiState is Loading until the album is read`() = runTest {
        val vm = subscribedViewModel(FakeAlbumRepository(cachedAlbum))

        assertEquals(DetailsUiState.Loading, vm.uiState.value)
    }

    @Test
    fun `uiState shows the album`() = runTest {
        val vm = subscribedViewModel(FakeAlbumRepository(cachedAlbum))

        cachedAlbum.emit(album)

        assertEquals(DetailsUiState.Success(album), vm.uiState.value)
    }

    @Test
    fun `uiState reads the album of the route`() = runTest {
        val repository = FakeAlbumRepository(cachedAlbum)

        subscribedViewModel(repository, albumId = 3)

        assertEquals(listOf(3), repository.readIds)
    }

    @Test
    fun `uiState follows the changes of the album`() = runTest {
        val vm = subscribedViewModel(FakeAlbumRepository(cachedAlbum))
        cachedAlbum.emit(album)
        val favorite = album.copy(isFavorite = true)

        cachedAlbum.emit(favorite)

        assertEquals(DetailsUiState.Success(favorite), vm.uiState.value)
    }

    @Test
    fun `uiState is Error when the album is not cached`() = runTest {
        val vm = subscribedViewModel(FakeAlbumRepository(cachedAlbum))

        cachedAlbum.emit(null)

        assertEquals(DetailsUiState.Error, vm.uiState.value)
    }

    @Test
    fun `uiState is Error when a refresh removes the album`() = runTest {
        val vm = subscribedViewModel(FakeAlbumRepository(cachedAlbum))
        cachedAlbum.emit(album)

        cachedAlbum.emit(null)

        assertEquals(DetailsUiState.Error, vm.uiState.value)
    }

    @Test
    fun `uiState is Error when the album cannot be read`() = runTest {
        val failure = flow<Album?> { throw IOException("database error") }

        val vm = subscribedViewModel(FakeAlbumRepository(failure))

        assertEquals(DetailsUiState.Error, vm.uiState.value)
    }

    @Test
    fun `onFavoriteClick toggles the album of the route`() = runTest {
        val repository = FakeAlbumRepository(cachedAlbum)
        val vm = subscribedViewModel(repository, albumId = 3)

        vm.onFavoriteClick()

        assertEquals(listOf(3), repository.toggledIds)
    }

    @Test
    fun `onFavoriteClick keeps the album when toggling fails`() = runTest {
        val repository = FakeAlbumRepository(cachedAlbum)
        repository.toggleFailure = IOException("database error")
        val vm = subscribedViewModel(repository)
        cachedAlbum.emit(album)

        vm.onFavoriteClick()

        assertEquals(DetailsUiState.Success(album), vm.uiState.value)
    }

    /** `uiState` only reads the album while it is collected. */
    private fun TestScope.subscribedViewModel(
        repository: AlbumRepository,
        albumId: Int = album.id,
    ): DetailsViewModel {
        val vm = DetailsViewModel(
            albumId = albumId,
            getAlbum = GetAlbumUseCase(repository),
            toggleFavorite = ToggleFavoriteUseCase(repository),
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        return vm
    }

    private class FakeAlbumRepository(
        private val album: Flow<Album?>,
    ) : AlbumRepository {

        val readIds = mutableListOf<Int>()
        val toggledIds = mutableListOf<Int>()
        var toggleFailure: Exception? = null

        override fun getPagedAlbums(): Flow<PagingData<Album>> = emptyFlow()

        override fun getFavoriteAlbums(): Flow<List<Album>> = emptyFlow()

        override fun getAlbum(id: Int): Flow<Album?> {
            readIds += id
            return album
        }

        override suspend fun loadAlbums() = Unit

        override suspend fun toggleFavorite(id: Int) {
            toggleFailure?.let { throw it }
            toggledIds += id
        }
    }
}
