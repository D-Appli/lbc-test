package fr.leboncoin.core.data.network.model

import kotlinx.serialization.Serializable

@Serializable
internal data class AlbumDto(
    val id: Int,
    val albumId: Int,
    val title: String,
    val url: String,
    val thumbnailUrl: String
)