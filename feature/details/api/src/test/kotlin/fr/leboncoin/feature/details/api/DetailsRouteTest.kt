package fr.leboncoin.feature.details.api

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DetailsRouteTest {

    @Test
    fun `navigateToDetails opens the details of the album`() {
        val backStack = NavBackStack<NavKey>(HomeRoute)

        val opened = backStack.navigateToDetails(albumId = 3)

        assertTrue(opened)
        assertEquals(listOf(HomeRoute, DetailsRoute(3)), backStack.toList())
    }

    @Test
    fun `navigateToDetails ignores a second click on the same album`() {
        val backStack = NavBackStack<NavKey>(HomeRoute)
        backStack.navigateToDetails(albumId = 3)

        val opened = backStack.navigateToDetails(albumId = 3)

        assertFalse(opened)
        assertEquals(listOf(HomeRoute, DetailsRoute(3)), backStack.toList())
    }

    @Test
    fun `navigateToDetails can open another album`() {
        val backStack = NavBackStack<NavKey>(HomeRoute)
        backStack.navigateToDetails(albumId = 3)

        val opened = backStack.navigateToDetails(albumId = 4)

        assertTrue(opened)
        assertEquals(listOf(HomeRoute, DetailsRoute(3), DetailsRoute(4)), backStack.toList())
    }

    private data object HomeRoute : NavKey
}
