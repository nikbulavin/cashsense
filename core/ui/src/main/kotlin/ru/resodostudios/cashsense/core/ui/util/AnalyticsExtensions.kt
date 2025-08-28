package ru.resodostudios.cashsense.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import ru.resodostudios.cashsense.core.analytics.AnalyticsEvent
import ru.resodostudios.cashsense.core.analytics.AnalyticsEvent.Param
import ru.resodostudios.cashsense.core.analytics.AnalyticsEvent.ParamKeys
import ru.resodostudios.cashsense.core.analytics.AnalyticsEvent.Types
import ru.resodostudios.cashsense.core.analytics.AnalyticsHelper
import ru.resodostudios.cashsense.core.analytics.LocalAnalyticsHelper

private fun AnalyticsHelper.logScreenView(screenName: String) {
    logEvent(
        AnalyticsEvent(
            type = Types.SCREEN_VIEW,
            extras = listOf(
                Param(ParamKeys.SCREEN_NAME, screenName),
            ),
        ),
    )
}

/**
 * A side-effect which records a screen view event.
 */
@Composable
fun TrackScreenViewEvent(
    screenName: String,
    analyticsHelper: AnalyticsHelper = LocalAnalyticsHelper.current,
) = DisposableEffect(Unit) {
    analyticsHelper.logScreenView(screenName)
    onDispose {}
}
