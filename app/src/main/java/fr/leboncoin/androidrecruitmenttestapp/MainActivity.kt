package fr.leboncoin.androidrecruitmenttestapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.adevinta.spark.SparkTheme
import dagger.hilt.android.AndroidEntryPoint
import fr.leboncoin.androidrecruitmenttestapp.navigation.AppNavDisplay
import fr.leboncoin.core.analytics.AnalyticsHelper
import fr.leboncoin.core.analytics.LocalAnalyticsHelper
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CompositionLocalProvider(LocalAnalyticsHelper provides analyticsHelper) {
                SparkTheme {
                    AppNavDisplay()
                }
            }
        }
    }
}