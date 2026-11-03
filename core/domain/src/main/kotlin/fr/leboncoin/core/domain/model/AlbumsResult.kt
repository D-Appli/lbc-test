package fr.leboncoin.core.domain.model

/** The cached [albums], with the status of their refresh from the network. */
data class AlbumsResult(
    val albums: List<Album>,
    val refreshStatus: RefreshStatus,
)

sealed interface RefreshStatus {
    data object InProgress : RefreshStatus
    data object Success : RefreshStatus
    data class Failure(val error: Throwable) : RefreshStatus
}
