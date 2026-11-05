package fr.leboncoin.core.analytics

import timber.log.Timber
import javax.inject.Inject

class AnalyticsHelper @Inject constructor() {

    fun trackSelection(itemId: String) {
        // Simulate some analytics logging
        Timber.d("Analytics: User selected item - $itemId")
    }

    fun trackScreenView(screenName: String) {
        // Simulate some analytics logging
        Timber.d("Analytics: Screen viewed - $screenName")
    }
}
