package fr.leboncoin.core.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.leboncoin.core.data.BuildConfig
import fr.leboncoin.core.data.network.api.AlbumApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import javax.inject.Qualifier
import javax.inject.Singleton

/** The [OkHttpClient] that downloads the images. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ImageHttpClient

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.HEADERS }
                )
            }
        }
        .build()

    /** Shares the connections and the threads of the main client, without logging the images. */
    @Provides
    @Singleton
    @ImageHttpClient
    fun provideImageHttpClient(okHttpClient: OkHttpClient): OkHttpClient = okHttpClient.newBuilder()
        .apply { interceptors().clear() }
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", IMAGE_USER_AGENT)
                .build()
            chain.proceed(request)
        }
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(AlbumApiService.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideAlbumApiService(retrofit: Retrofit): AlbumApiService = retrofit.create()

    private const val IMAGE_USER_AGENT = "LeboncoinApp/1.0"
}
