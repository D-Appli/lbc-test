package fr.leboncoin.core.analytics

import androidx.compose.runtime.staticCompositionLocalOf

/** Gives the composables of any feature access to the [AnalyticsHelper] of the activity. */
val LocalAnalyticsHelper = staticCompositionLocalOf<AnalyticsHelper> {
    error("No AnalyticsHelper provided")
}
