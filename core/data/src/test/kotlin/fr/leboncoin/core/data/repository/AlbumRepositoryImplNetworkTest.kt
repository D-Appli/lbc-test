package fr.leboncoin.core.data.repository

import fr.leboncoin.core.data.di.NetworkModule
import fr.leboncoin.core.data.network.api.AlbumApiService
import fr.leboncoin.core.database.dao.AlbumDao
import fr.leboncoin.core.database.entity.AlbumEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Runs [AlbumRepositoryImpl] with the real Retrofit setup of the app. Only the network call
 * is replaced: an interceptor answers with a local JSON file.
 */
class AlbumRepositoryImplNetworkTest {

    private var request: Request? = null
    private var responseFile = "albums.json"

    private val localResponse = Interceptor { chain ->
        request = chain.request()
        Response.Builder()
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(readResource(responseFile).toResponseBody("application/json".toMediaType()))
            .build()
    }

    private val albumDao = mockk<AlbumDao>()
    private val repository = AlbumRepositoryImpl(albumApiService(), albumDao)

    @Test
    fun `loadAlbums writes the albums of the response to the cache`() = runTest {
        coEvery { albumDao.replaceAllIfChanged(any()) } returns true

        repository.loadAlbums()

        coVerify(exactly = 1) {
            albumDao.replaceAllIfChanged(
                listOf(
                    AlbumEntity(
                        id = 1,
                        albumId = 1,
                        title = "accusamus beatae ad facilis cum similique qui sunt",
                        url = "https://placehold.co/600x600/92c952/white/png",
                        thumbnailUrl = "https://placehold.co/150x150/92c952/white/png",
                    ),
                    AlbumEntity(
                        id = 2,
                        albumId = 1,
                        title = "reprehenderit est deserunt velit ipsam",
                        url = "https://placehold.co/600x600/771796/white/png",
                        thumbnailUrl = "https://placehold.co/150x150/771796/white/png",
                    ),
                )
            )
        }
    }

    @Test
    fun `loadAlbums requests the albums url`() = runTest {
        coEvery { albumDao.replaceAllIfChanged(any()) } returns true

        repository.loadAlbums()

        assertEquals(
            "https://static.leboncoin.fr/img/shared/technical-test.json",
            request?.url.toString(),
        )
    }

    @Test
    fun `loadAlbums drops an album with a missing field of the response`() = runTest {
        responseFile = "bad-albums1.json"
        coEvery { albumDao.replaceAllIfChanged(any()) } returns true

        repository.loadAlbums()

        coVerify(exactly = 1) {
            albumDao.replaceAllIfChanged(
                listOf(
                    AlbumEntity(
                        id = 2,
                        albumId = 1,
                        title = "reprehenderit est deserunt velit ipsam",
                        url = "https://placehold.co/600x600/771796/white/png",
                        thumbnailUrl = "https://placehold.co/150x150/771796/white/png",
                    ),
                )
            )
        }
    }

    @Test(expected = SerializationException::class)
    fun `loadAlbums throws and keeps the cache when the response is malformed`() = runTest {
        responseFile = "mal-formatted-albums.json"

        try {
            repository.loadAlbums()
        } finally {
            coVerify(exactly = 0) { albumDao.replaceAllIfChanged(any()) }
        }
    }

    private fun albumApiService(): AlbumApiService {
        val client = OkHttpClient.Builder().addInterceptor(localResponse).build()
        val retrofit = NetworkModule.provideRetrofit(client, NetworkModule.provideJson())
        return NetworkModule.provideAlbumApiService(retrofit)
    }

    private fun readResource(name: String): String =
        checkNotNull(javaClass.classLoader?.getResource(name)) { "Missing test resource $name" }
            .readText()
}
