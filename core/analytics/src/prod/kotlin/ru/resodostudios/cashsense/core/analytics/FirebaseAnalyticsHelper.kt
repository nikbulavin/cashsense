package ru.resodostudios.cashsense.core.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import javax.inject.Inject

/**
 * Implementation of [AnalyticsHelper] which logs events to a Firebase backend.
 */
internal class FirebaseAnalyticsHelper @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsHelper {

    override fun logEvent(event: AnalyticsEvent) {
        firebaseAnalytics.logEvent(event.type) {
            event.extras.forEach { param ->
                param(
                    key = param.key.take(40),
                    value = param.value.take(100),
                )
            }
        }
    }
}
