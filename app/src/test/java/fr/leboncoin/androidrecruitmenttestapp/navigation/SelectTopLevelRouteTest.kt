package fr.leboncoin.androidrecruitmenttestapp.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import fr.leboncoin.feature.albums.api.AlbumsRoute
import fr.leboncoin.feature.favorites.api.FavoritesRoute
import org.junit.Assert.assertEquals
import org.junit.Test

class SelectTopLevelRouteTest {

    @Test
    fun `selecting favorites adds it above albums`() {
        val backStack = backStackOf(AlbumsRoute)

        backStack.selectTopLevelRoute(FavoritesRoute)

        assertEquals(listOf(AlbumsRoute, FavoritesRoute), backStack.toList())
    }

    @Test
    fun `selecting albums from favorites goes back to albums`() {
        val backStack = backStackOf(AlbumsRoute, FavoritesRoute)

        backStack.selectTopLevelRoute(AlbumsRoute)

        assertEquals(listOf(AlbumsRoute), backStack.toList())
    }

    @Test
    fun `selecting albums again keeps the back stack`() {
        val backStack = backStackOf(AlbumsRoute)

        backStack.selectTopLevelRoute(AlbumsRoute)

        assertEquals(listOf(AlbumsRoute), backStack.toList())
    }

    @Test
    fun `selecting favorites again keeps the back stack`() {
        val backStack = backStackOf(AlbumsRoute, FavoritesRoute)

        backStack.selectTopLevelRoute(FavoritesRoute)

        assertEquals(listOf(AlbumsRoute, FavoritesRoute), backStack.toList())
    }

    @Test
    fun `selecting albums removes every route above it`() {
        val backStack = backStackOf(AlbumsRoute, SubRoute, FavoritesRoute)

        backStack.selectTopLevelRoute(AlbumsRoute)

        assertEquals(listOf(AlbumsRoute), backStack.toList())
    }

    @Test
    fun `selecting favorites replaces the routes above albums`() {
        val backStack = backStackOf(AlbumsRoute, SubRoute)

        backStack.selectTopLevelRoute(FavoritesRoute)

        assertEquals(listOf(AlbumsRoute, FavoritesRoute), backStack.toList())
    }

    private fun backStackOf(vararg routes: NavKey) = NavBackStack(*routes)

    private data object SubRoute : NavKey
}
