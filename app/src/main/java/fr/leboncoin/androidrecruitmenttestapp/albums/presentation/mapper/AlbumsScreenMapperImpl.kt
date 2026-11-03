package fr.leboncoin.androidrecruitmenttestapp.albums.presentation.mapper

import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.model.AlbumUiModel
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.model.AlbumsScreenState
import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.model.AlbumsScreenState.Content
import fr.leboncoin.data.network.model.AlbumDto
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class AlbumsScreenMapperImpl @Inject constructor() : AlbumsScreenMapper {

    override fun map(param: List<AlbumDto>): AlbumsScreenState {
        val albums = param.map {
            AlbumUiModel(
                id = it.id,
                albumId = it.albumId,
                title = it.title,
                thumbnailUrl = it.thumbnailUrl,
            )
        }.toImmutableList()
        return Content(albums = albums)
    }
}
