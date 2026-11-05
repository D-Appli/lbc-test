package fr.leboncoin.core.data.network.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * All fields are optional, so that one invalid album cannot fail the parsing of the whole
 * response. The invalid albums are dropped by the mapper.
 */
// Only for Android Studio: its older Kotlin plugin reports a false opt-in error on this class,
// while Gradle compiles it without. To remove once Studio bundles the Kotlin version of the project.
@OptIn(InternalSerializationApi::class)
@Serializable
internal data class AlbumDto(
    val id: Int? = null,
    val albumId: Int? = null,
    val title: String? = null,
    val url: String? = null,
    val thumbnailUrl: String? = null,
)
