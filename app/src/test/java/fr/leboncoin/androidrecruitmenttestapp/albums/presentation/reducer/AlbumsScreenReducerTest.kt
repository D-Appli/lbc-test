package fr.leboncoin.androidrecruitmenttestapp.albums.presentation.reducer

import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.mapper.AlbumsScreenMapperImpl
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.model.AlbumUiModel
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.model.AlbumsScreenState
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.model.CallToActions
import fr.leboncoin.androidrecruitmenttestapp.dispatcher.TestDispatcherProvider
import fr.leboncoin.data.network.model.AlbumDto
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AlbumsScreenReducerTest {

    private val sut = AlbumsScreenReducerImpl(
        dispatcherProvider = TestDispatcherProvider,
        mapper = AlbumsScreenMapperImpl(),
    )

    private val albums = listOf(
        AlbumDto(id = 1, albumId = 2, title = "t", url = "u", thumbnailUrl = "tu")
    )

    private val content = AlbumsScreenState.Content(
        albums = persistentListOf(
            AlbumUiModel(id = 1, albumId = 2, title = "t", thumbnailUrl = "tu")
        ),
    )

    private val onRetry: () -> Unit = {}

    @Test
    fun `verify initial state is None`() {
        assertEquals(AlbumsScreenState.None, sut.stateFlow.value)
    }

    @Test
    fun `verify SetLoading without content sets Loading`() = runTest {
        // when
        sut.update(AlbumsScreenAction.SetLoading)

        // then
        assertEquals(AlbumsScreenState.Loading, sut.stateFlow.value)
    }

    @Test
    fun `verify SetContent maps albums to Content`() = runTest {
        // when
        sut.update(AlbumsScreenAction.SetContent(albums))

        // then
        assertEquals(content, sut.stateFlow.value)
    }

    @Test
    fun `verify SetError without content sets Error`() = runTest {
        // when
        sut.update(AlbumsScreenAction.SetError(CallToActions(onRetry)))

        // then
        assertEquals(AlbumsScreenState.Error(onRetry), sut.stateFlow.value)
    }

    @Test
    fun `verify SetLoading with content keeps the albums`() = runTest {
        // given
        sut.update(AlbumsScreenAction.SetContent(albums))

        // when
        sut.update(AlbumsScreenAction.SetLoading)

        // then
        assertEquals(content.copy(isLoading = true), sut.stateFlow.value)
    }

    @Test
    fun `verify SetError with content keeps the albums`() = runTest {
        // given
        sut.update(AlbumsScreenAction.SetContent(albums))
        sut.update(AlbumsScreenAction.SetLoading)

        // when
        sut.update(AlbumsScreenAction.SetError(CallToActions(onRetry)))

        // then
        assertEquals(content, sut.stateFlow.value)
    }
}
