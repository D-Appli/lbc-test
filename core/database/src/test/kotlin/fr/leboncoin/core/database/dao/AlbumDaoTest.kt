package fr.leboncoin.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import fr.leboncoin.core.database.AppDatabase
import fr.leboncoin.core.database.entity.AlbumEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AlbumDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: AlbumDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = database.albumDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `replaceAllIfChanged inserts the albums into an empty cache`() = runTest {
        val changed = dao.replaceAllIfChanged(listOf(album(2), album(1)))

        assertTrue(changed)
        assertEquals(listOf(album(1), album(2)), dao.getAll())
    }

    @Test
    fun `replaceAllIfChanged does not update the cache with the same albums`() = runTest {
        dao.replaceAllIfChanged(listOf(album(1), album(2)))

        val changed = dao.replaceAllIfChanged(listOf(album(2), album(1)))

        assertFalse(changed)
    }

    @Test
    fun `replaceAllIfChanged replaces the cache with different albums`() = runTest {
        dao.replaceAllIfChanged(listOf(album(1), album(2)))

        val changed = dao.replaceAllIfChanged(listOf(album(2, title = "new"), album(3)))

        assertTrue(changed)
        assertEquals(listOf(album(2, title = "new"), album(3)), dao.getAll())
    }

    @Test
    fun `replaceAllIfChanged keeps the favorites when the albums change`() = runTest {
        dao.replaceAllIfChanged(listOf(album(1), album(2)))
        dao.toggleFavorite(1)

        val changed = dao.replaceAllIfChanged(listOf(album(1, title = "new"), album(2), album(3)))

        assertTrue(changed)
        assertEquals(
            listOf(album(1, title = "new", isFavorite = true), album(2), album(3)),
            dao.getAll(),
        )
    }

    @Test
    fun `replaceAllIfChanged does not update the cache when only the favorites differ`() = runTest {
        dao.replaceAllIfChanged(listOf(album(1), album(2)))
        dao.toggleFavorite(1)

        val changed = dao.replaceAllIfChanged(listOf(album(1), album(2)))

        assertFalse(changed)
        assertEquals(listOf(album(1, isFavorite = true), album(2)), dao.getAll())
    }

    @Test
    fun `replaceAllIfChanged loses the favorite of an album that is no longer returned`() = runTest {
        dao.replaceAllIfChanged(listOf(album(1), album(2)))
        dao.toggleFavorite(1)

        dao.replaceAllIfChanged(listOf(album(2)))
        dao.replaceAllIfChanged(listOf(album(1), album(2)))

        assertEquals(listOf(album(1), album(2)), dao.getAll())
    }

    @Test
    fun `replaceAllIfChanged ignores the favorite value of the new albums`() = runTest {
        dao.replaceAllIfChanged(listOf(album(1, isFavorite = true)))

        assertEquals(listOf(album(1)), dao.getAll())
    }

    @Test
    fun `toggleFavorite adds the album to the favorites`() = runTest {
        dao.replaceAllIfChanged(listOf(album(1)))

        dao.toggleFavorite(1)

        assertEquals(listOf(album(1, isFavorite = true)), dao.getAll())
    }

    @Test
    fun `toggleFavorite removes the album from the favorites`() = runTest {
        dao.replaceAllIfChanged(listOf(album(1)))
        dao.toggleFavorite(1)

        dao.toggleFavorite(1)

        assertEquals(listOf(album(1)), dao.getAll())
    }

    @Test
    fun `toggleFavorite only changes the given album`() = runTest {
        dao.replaceAllIfChanged(listOf(album(1), album(2)))

        dao.toggleFavorite(2)

        assertEquals(listOf(album(1), album(2, isFavorite = true)), dao.getAll())
    }

    @Test
    fun `observeFavorites emits the favorite albums only, ordered by id`() = runTest {
        dao.replaceAllIfChanged(listOf(album(1), album(2), album(3)))
        dao.toggleFavorite(3)
        dao.toggleFavorite(1)

        assertEquals(
            listOf(album(1, isFavorite = true), album(3, isFavorite = true)),
            dao.observeFavorites().first(),
        )
    }

    @Test
    fun `observeFavorites emits an empty list without favorites`() = runTest {
        dao.replaceAllIfChanged(listOf(album(1)))

        assertEquals(emptyList<AlbumEntity>(), dao.observeFavorites().first())
    }

    @Test
    fun `observeFavorites emits again when a favorite changes`() = runTest {
        dao.replaceAllIfChanged(listOf(album(1)))
        var toggled = false

        val emissions = dao.observeFavorites()
            .onEach {
                if (!toggled) {
                    toggled = true
                    dao.toggleFavorite(1)
                }
            }
            .take(2)
            .toList()

        assertEquals(listOf(emptyList(), listOf(album(1, isFavorite = true))), emissions)
    }

    @Test
    fun `observeAlbum emits the album with this id`() = runTest {
        dao.replaceAllIfChanged(listOf(album(1), album(2)))

        assertEquals(album(2), dao.observeAlbum(2).first())
    }

    @Test
    fun `observeAlbum emits null when the album is not cached`() = runTest {
        dao.replaceAllIfChanged(listOf(album(1), album(3)))

        assertNull(dao.observeAlbum(2).first())
    }

    @Test
    fun `observeAlbum emits again when the album changes`() = runTest {
        dao.replaceAllIfChanged(listOf(album(1)))
        var toggled = false

        val emissions = dao.observeAlbum(1)
            .onEach {
                if (!toggled) {
                    toggled = true
                    dao.toggleFavorite(1)
                }
            }
            .take(2)
            .toList()

        assertEquals(listOf(album(1), album(1, isFavorite = true)), emissions)
    }

    private fun album(
        id: Int,
        title: String = "title $id",
        isFavorite: Boolean = false,
    ) = AlbumEntity(
        id = id,
        albumId = 1,
        title = title,
        url = "url $id",
        thumbnailUrl = "thumbnail $id",
        isFavorite = isFavorite,
    )
}
