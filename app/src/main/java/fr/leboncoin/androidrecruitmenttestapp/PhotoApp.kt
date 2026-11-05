package fr.leboncoin.androidrecruitmenttestapp

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import fr.leboncoin.core.data.di.ImageHttpClient
import okhttp3.OkHttpClient
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class PhotoApp : Application(), SingletonImageLoader.Factory {

    @Inject
    @ImageHttpClient
    lateinit var imageHttpClient: Lazy<OkHttpClient>

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    /** The memory cache keeps its default size, a share of the memory of the device. */
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .crossfade(true)
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(IMAGE_DISK_CACHE_SIZE_BYTES)
                    .build()
            }
            .components {
                add(OkHttpNetworkFetcherFactory(callFactory = { imageHttpClient.get() }))
            }
            .build()

    private companion object {
        // Enough for all the images of the albums: 5000 thumbnails and full size images.
        const val IMAGE_DISK_CACHE_SIZE_BYTES = 100L * 1024 * 1024
    }
}
