package fr.leboncoin.core.domain.usecase

import androidx.paging.PagingData
import fr.leboncoin.core.domain.model.Album
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class GetPagedAlbumsUseCaseTest {

    @Test
    fun `returns the paged albums of the repository`() {
        val pagedAlbums = flowOf(PagingData.from(listOf<Album>()))
        val repository = FakeAlbumRepository(pagedAlbums = pagedAlbums)

        assertSame(pagedAlbums, GetPagedAlbumsUseCase(repository)())
    }

    @Test
    fun `does not load the albums from the network`() {
        val repository = FakeAlbumRepository()

        GetPagedAlbumsUseCase(repository)()

        assertEquals(0, repository.loadCount)
    }
}
