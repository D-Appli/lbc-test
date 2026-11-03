package fr.leboncoin.androidrecruitmenttestapp.albums.presentation.viewmodel

import app.cash.turbine.test
import fr.leboncoin.androidrecruitmenttestapp.MainDispatcherRule
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.mapper.AlbumsScreenMapperImpl
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.model.AlbumUiModel
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.model.AlbumsScreenState
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.reducer.AlbumsScreenReducerImpl
import fr.leboncoin.androidrecruitmenttestapp.dispatcher.TestDispatcherProvider
import fr.leboncoin.androidrecruitmenttestapp.dispatcher.testDispatcher
import fr.leboncoin.data.network.api.AlbumApiService
import fr.leboncoin.data.network.model.AlbumDto
import fr.leboncoin.data.repository.AlbumRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class AlbumsScreenViewModelTest {

    // stateIn uses viewModelScope, which runs on Dispatchers.Main
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private val albums = listOf(
        AlbumDto(id = 1, albumId = 2, title = "t", url = "u", thumbnailUrl = "tu")
    )

    private val expectedContent = AlbumsScreenState.Content(
        albums = persistentListOf(
            AlbumUiModel(id = 1, albumId = 2, title = "t", thumbnailUrl = "tu")
        ),
    )

    private fun getSut(apiService: AlbumApiService): AlbumsScreenViewModel {
        val reducer = AlbumsScreenReducerImpl(
            dispatcherProvider = TestDispatcherProvider,
            mapper = AlbumsScreenMapperImpl(),
        )
        return AlbumsScreenViewModelImpl(
            dispatcherProvider = TestDispatcherProvider,
            reducer = reducer,
            repository = AlbumRepository(apiService),
        )
    }

    @Test
    fun `verify albums are not loaded until the state is collected`() = runTest {
        // given
        val apiService = mockk<AlbumApiService>()
        coEvery { apiService.getAlbums() } returns albums

        // when
        val sut = getSut(apiService)

        // then
        assertEquals(AlbumsScreenState.None, sut.stateFlow.value)
        coVerify(exactly = 0) { apiService.getAlbums() }
    }

    @Test
    fun `verify vm emits content state when collected`() = runTest {
        // given
        val apiService = mockk<AlbumApiService>()
        coEvery { apiService.getAlbums() } returns albums

        // when
        val sut = getSut(apiService)

        // then
        sut.stateFlow.test {
            assertEquals(expectedContent, awaitItem())
            expectNoEvents()
        }
        coVerify(exactly = 1) { apiService.getAlbums() }
    }

    @Test
    fun `verify vm emits error state when the request fails`() = runTest {
        // given
        val apiService = mockk<AlbumApiService>()
        coEvery { apiService.getAlbums() } throws IOException("network down")

        // when
        val sut = getSut(apiService)

        // then
        sut.stateFlow.test {
            assertTrue(awaitItem() is AlbumsScreenState.Error)
            expectNoEvents()
        }
    }

    @Test
    fun `verify retry after an error reloads the albums`() = runTest {
        // given
        val apiService = mockk<AlbumApiService>()
        coEvery { apiService.getAlbums() } throws IOException("network down") andThen albums
        val sut = getSut(apiService)

        sut.stateFlow.test {
            val error = awaitItem() as AlbumsScreenState.Error

            // when
            error.onRetry()

            // then
            assertEquals(expectedContent, expectMostRecentItem())
        }
        coVerify(exactly = 2) { apiService.getAlbums() }
    }
}
