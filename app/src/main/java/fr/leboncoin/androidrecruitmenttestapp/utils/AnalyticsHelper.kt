package fr.leboncoin.androidrecruitmenttestapp.utils

import javax.inject.Inject

class AnalyticsHelper @Inject constructor() {

    fun trackSelection(itemId: String) {
        // Simulate some analytics logging
        println("Analytics: User selected item - $itemId")
    }

    fun trackScreenView(screenName: String) {
        // Simulate some analytics logging
        println("Analytics: Screen viewed - $screenName")
    }
}
