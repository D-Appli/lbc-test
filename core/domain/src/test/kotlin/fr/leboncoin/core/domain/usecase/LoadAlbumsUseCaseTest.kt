package fr.leboncoin.core.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException

class LoadAlbumsUseCaseTest {

    @Test
    fun `loads the albums once`() = runTest {
        val repository = FakeAlbumRepository()

        LoadAlbumsUseCase(repository)()

        assertEquals(1, repository.loadCount)
    }

    @Test(expected = IOException::class)
    fun `throws when the albums cannot be loaded`() = runTest {
        val repository = FakeAlbumRepository(onLoad = { throw IOException("network down") })

        LoadAlbumsUseCase(repository)()
    }
}
