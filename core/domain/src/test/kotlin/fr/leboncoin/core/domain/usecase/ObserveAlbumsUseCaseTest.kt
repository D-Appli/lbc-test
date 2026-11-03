package fr.leboncoin.core.domain.usecase

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import fr.leboncoin.core.domain.model.Album
import fr.leboncoin.core.domain.model.AlbumsResult
import fr.leboncoin.core.domain.model.RefreshStatus
import fr.leboncoin.core.domain.repository.AlbumRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException

class ObserveAlbumsUseCaseTest {

    private val cached = listOf(album(1))
    private val remote = listOf(album(1), album(2))

    @Test
    fun invoke_emitsCacheFirst_thenRemoteAlbumsAfterRefresh() = runTest {
        val repository = FakeAlbumRepository(cached, onRefresh = { remote })

        ObserveAlbumsUseCase(repository)().test {
            assertEquals(AlbumsResult(cached, RefreshStatus.InProgress), awaitItem())
            repository.completeRefresh()
            // `combine` may skip the intermediate (remote, InProgress) result.
            assertEquals(AlbumsResult(remote, RefreshStatus.Success), awaitRefreshEnd())
        }
    }

    @Test
    fun invoke_whenRefreshFails_keepsCacheAndReportsError() = runTest {
        val error = IOException("network down")
        val repository = FakeAlbumRepository(cached, onRefresh = { throw error })

        ObserveAlbumsUseCase(repository)().test {
            assertEquals(AlbumsResult(cached, RefreshStatus.InProgress), awaitItem())
            repository.completeRefresh()
            assertEquals(AlbumsResult(cached, RefreshStatus.Failure(error)), awaitRefreshEnd())
        }
    }

    @Test
    fun invoke_whenRemoteAlbumsUnchanged_onlyReportsSuccess() = runTest {
        val repository = FakeAlbumRepository(cached, onRefresh = { cached })

        ObserveAlbumsUseCase(repository)().test {
            assertEquals(AlbumsResult(cached, RefreshStatus.InProgress), awaitItem())
            repository.completeRefresh()
            assertEquals(AlbumsResult(cached, RefreshStatus.Success), awaitRefreshEnd())
            expectNoEvents()
        }
    }

    /** Skips the results emitted while the refresh is in progress. */
    private suspend fun ReceiveTurbine<AlbumsResult>.awaitRefreshEnd(): AlbumsResult {
        var result = awaitItem()
        while (result.refreshStatus == RefreshStatus.InProgress) result = awaitItem()
        return result
    }

    private class FakeAlbumRepository(
        cached: List<Album>,
        private val onRefresh: () -> List<Album>,
    ) : AlbumRepository {

        private val cache = MutableStateFlow(cached)
        private val refreshGate = CompletableDeferred<Unit>()

        fun completeRefresh() = refreshGate.complete(Unit)

        override fun observeAlbums(): Flow<List<Album>> = cache

        override suspend fun refreshAlbums() {
            refreshGate.await()
            // Like the real cache, only an actual change is emitted.
            cache.value = onRefresh()
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
