package fr.leboncoin.androidrecruitmenttestapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.leboncoin.data.network.model.AlbumDto
import fr.leboncoin.data.repository.AlbumRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val repository: AlbumRepository,
) : ViewModel() {

    private val _albums = MutableSharedFlow<List<AlbumDto>>()
    val albums: SharedFlow<List<AlbumDto>> = _albums

    fun loadAlbums() {
        viewModelScope.launch {
            try {
                _albums.emit(repository.getAllAlbums())
            } catch (e: CancellationException) {
                // Never swallow cancellation, or the coroutine keeps running after its scope is cancelled.
                throw e
            } catch (_: Exception) {
                /* TODO: Handle errors */
            }
        }
    }
}
