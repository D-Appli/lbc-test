package fr.leboncoin.core.domain.usecase

import fr.leboncoin.core.domain.model.AlbumsResult
import fr.leboncoin.core.domain.model.RefreshStatus
import fr.leboncoin.core.domain.repository.AlbumRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Emits the cached albums first, and refreshes them from the network at the same time.
 * The cache is the single source of truth: a successful refresh updates it, which emits
 * the new albums, while a failed refresh keeps the cached albums and reports the error.
 */
class ObserveAlbumsUseCase @Inject constructor(
    private val repository: AlbumRepository,
) {

    operator fun invoke(): Flow<AlbumsResult> =
        combine(repository.observeAlbums(), refresh()) { albums, refreshStatus ->
            AlbumsResult(albums, refreshStatus)
        }

    private fun refresh(): Flow<RefreshStatus> = flow {
        emit(RefreshStatus.InProgress)
        // The result is emitted outside the try, so downstream exceptions are not caught.
        val status = try {
            repository.refreshAlbums()
            RefreshStatus.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            RefreshStatus.Failure(e)
        }
        emit(status)
    }
}
