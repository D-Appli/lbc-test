package fr.leboncoin.androidrecruitmenttestapp

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.leboncoin.data.network.model.AlbumDto
import fr.leboncoin.data.repository.AlbumRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val repository: AlbumRepository,
) : ViewModel() {

    private val _albums = MutableSharedFlow<List<AlbumDto>>()
    val albums: SharedFlow<List<AlbumDto>> = _albums

    fun loadAlbums() {
        GlobalScope.launch {
            try {
                _albums.emit(repository.getAllAlbums())
            } catch (_: Exception) { /* TODO: Handle errors */ }
        }
    }
}