package fr.leboncoin.feature.favorites.presentation

import androidx.paging.PagingData
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.core.domain.repository.AlbumRepository
import fr.leboncoin.core.domain.usecase.GetFavoriteAlbumsUseCase
import fr.leboncoin.core.domain.usecase.ToggleFavoriteUseCase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
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
class FavoritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val favorites = MutableSharedFlow<List<Album>>()
    private val failure = flow<List<Album>> { throw IOException("database error") }

    @Test
    fun `uiState is Loading until the favorites are read`() = runTest {
        val vm = subscribedViewModel(FakeAlbumRepository(favorites))

        assertEquals(FavoritesUiState.Loading, vm.uiState.value)
    }

    @Test
    fun `uiState shows the favorites`() = runTest {
        val vm = subscribedViewModel(FakeAlbumRepository(favorites))

        favorites.emit(listOf(album(1), album(2)))

        assertEquals(success(album(1), album(2)), vm.uiState.value)
    }

    @Test
    fun `uiState is an empty Success without favorites`() = runTest {
        val vm = subscribedViewModel(FakeAlbumRepository(favorites))

        favorites.emit(emptyList())

        assertEquals(FavoritesUiState.Success(persistentListOf()), vm.uiState.value)
    }

    @Test
    fun `uiState follows the changes of the favorites`() = runTest {
        val vm = subscribedViewModel(FakeAlbumRepository(favorites))
        favorites.emit(listOf(album(1), album(2)))

        favorites.emit(listOf(album(2)))

        assertEquals(success(album(2)), vm.uiState.value)
    }

    @Test
    fun `uiState is Error when the favorites cannot be read`() = runTest {
        val vm = subscribedViewModel(FakeAlbumRepository(failure))

        assertEquals(FavoritesUiState.Error, vm.uiState.value)
    }

    @Test
    fun `onRetryClick shows Loading while the favorites are read again`() = runTest {
        val vm = subscribedViewModel(FakeAlbumRepository(failure, favorites))

        vm.onRetryClick()

        assertEquals(FavoritesUiState.Loading, vm.uiState.value)
    }

    @Test
    fun `onRetryClick shows the favorites after an error`() = runTest {
        val vm = subscribedViewModel(FakeAlbumRepository(failure, favorites))
        vm.onRetryClick()

        favorites.emit(listOf(album(1)))

        assertEquals(success(album(1)), vm.uiState.value)
    }

    @Test
    fun `onRetryClick shows Error when the favorites still cannot be read`() = runTest {
        val vm = subscribedViewModel(FakeAlbumRepository(failure, failure))

        vm.onRetryClick()

        assertEquals(FavoritesUiState.Error, vm.uiState.value)
    }

    @Test
    fun `onFavoriteClick toggles the album`() = runTest {
        val repository = FakeAlbumRepository(favorites)
        val vm = subscribedViewModel(repository)

        vm.onFavoriteClick(album(3))

        assertEquals(listOf(3), repository.toggledIds)
    }

    /** `uiState` only reads the favorites while it is collected. */
    private fun TestScope.subscribedViewModel(repository: AlbumRepository): FavoritesViewModel {
        val vm = FavoritesViewModel(
            getFavoriteAlbums = GetFavoriteAlbumsUseCase(repository),
            toggleFavorite = ToggleFavoriteUseCase(repository),
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        return vm
    }

    private fun success(vararg albums: Album) =
        FavoritesUiState.Success(albums.toList().toImmutableList())

    /** Each read of the favorites uses the next flow of [favoriteFlows]. */
    private class FakeAlbumRepository(
        private vararg val favoriteFlows: Flow<List<Album>>,
    ) : AlbumRepository {

        private var reads = 0
        val toggledIds = mutableListOf<Int>()

        override fun getPagedAlbums(): Flow<PagingData<Album>> = emptyFlow()

        override fun getFavoriteAlbums(): Flow<List<Album>> = favoriteFlows[reads++]

        override suspend fun loadAlbums() = Unit

        override suspend fun toggleFavorite(id: Int) {
            toggledIds += id
        }
    }

    private fun album(id: Int) = Album(
        id = id,
        albumId = 1,
        title = "title $id",
        url = "url $id",
        thumbnailUrl = "thumbnail $id",
        isFavorite = true,
    )
}
