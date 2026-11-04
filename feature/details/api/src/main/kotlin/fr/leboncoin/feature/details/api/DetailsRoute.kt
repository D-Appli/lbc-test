package fr.leboncoin.feature.details.api

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class DetailsRoute(val albumId: Int) : NavKey

/**
 * Ignores a second click on the same album, which would stack the screen twice.
 *
 * @return true if the screen was opened.
 */
fun NavBackStack<NavKey>.navigateToDetails(albumId: Int): Boolean {
    val route = DetailsRoute(albumId)
    if (last() == route) return false
    add(route)
    return true
}
