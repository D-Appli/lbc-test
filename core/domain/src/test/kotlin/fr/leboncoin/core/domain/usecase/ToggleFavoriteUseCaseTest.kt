package fr.leboncoin.core.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ToggleFavoriteUseCaseTest {

    @Test
    fun `toggles the favorite of the given album`() = runTest {
        val repository = FakeAlbumRepository()

        ToggleFavoriteUseCase(repository)(id = 3)

        assertEquals(listOf(3), repository.toggledIds)
    }
}
