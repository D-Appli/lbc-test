package fr.leboncoin.androidrecruitmenttestapp.albums.presentation.model

import androidx.compose.runtime.Stable

@Stable
data class AlbumUiModel(
    val id: Int,
    val albumId: Int,
    val title: String,
    val thumbnailUrl: String,
)
