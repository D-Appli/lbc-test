package fr.leboncoin.libs.albums.ui

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.adevinta.spark.SparkTheme
import fr.leboncoin.core.domain.model.Album
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AlbumItemTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val album = Album(
        id = 7,
        albumId = 3,
        title = "accusamus beatae",
        url = "https://example.com/600.png",
        thumbnailUrl = "https://example.com/150.png",
    )

    private val selectedAlbums = mutableListOf<Album>()
    private val favoriteClicks = mutableListOf<Album>()

    @Test
    fun `shows the title and the labels of the album`() {
        setAlbumItem(album)

        composeRule.onNodeWithText("accusamus beatae", useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithText("Album #3", useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithText("Track #7", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun `clicking the card selects the album`() {
        setAlbumItem(album)

        // The text is the unmerged node: the center of the merged card may be on a label.
        composeRule.onNodeWithText("accusamus beatae", useUnmergedTree = true).performClick()

        assertEquals(listOf(album), selectedAlbums)
        assertEquals(emptyList<Album>(), favoriteClicks)
    }

    @Test
    fun `clicking the favorite chip toggles the favorite without selecting the album`() {
        setAlbumItem(album)

        composeRule.onNodeWithContentDescription(addToFavorites()).performClick()

        assertEquals(listOf(album), favoriteClicks)
        assertEquals(emptyList<Album>(), selectedAlbums)
    }

    @Test
    fun `clicking the album label does nothing`() {
        setAlbumItem(album)

        composeRule.onNodeWithText("Album #3", useUnmergedTree = true).performClick()

        assertEquals(emptyList<Album>(), selectedAlbums)
        assertEquals(emptyList<Album>(), favoriteClicks)
    }

    @Test
    fun `an album out of the favorites offers to add it`() {
        setAlbumItem(album.copy(isFavorite = false))

        composeRule.onNodeWithContentDescription(addToFavorites()).assertExists()
        composeRule.onNodeWithContentDescription(removeFromFavorites()).assertDoesNotExist()
    }

    @Test
    fun `a favorite album offers to remove it`() {
        setAlbumItem(album.copy(isFavorite = true))

        composeRule.onNodeWithContentDescription(removeFromFavorites()).assertExists()
        composeRule.onNodeWithContentDescription(addToFavorites()).assertDoesNotExist()
    }

    private fun setAlbumItem(album: Album) {
        composeRule.setContent {
            SparkTheme {
                AlbumItem(
                    album = album,
                    onItemSelected = { selectedAlbums += it },
                    onFavoriteClick = { favoriteClicks += it },
                )
            }
        }
    }

    private fun addToFavorites() = context.getString(R.string.album_add_to_favorites)

    private fun removeFromFavorites() = context.getString(R.string.album_remove_from_favorites)
}
