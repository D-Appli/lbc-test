package fr.leboncoin.androidrecruitmenttestapp.albums.presentation.mapper

import fr.leboncoin.androidrecruitmenttestapp.albums.presentation.model.AlbumsScreenState
import fr.leboncoin.core.kotlin.mapper.Mapper
import fr.leboncoin.data.network.model.AlbumDto

interface AlbumsScreenMapper : Mapper<List<AlbumDto>, AlbumsScreenState>
