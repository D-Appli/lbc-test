package fr.leboncoin.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import fr.leboncoin.core.database.AppDatabase
import fr.leboncoin.core.database.entity.AlbumEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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

    private fun album(id: Int, title: String = "title $id") = AlbumEntity(
        id = id,
        albumId = 1,
        title = title,
        url = "url $id",
        thumbnailUrl = "thumbnail $id",
    )
}
