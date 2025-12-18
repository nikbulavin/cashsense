package ru.resodostudios.cashsense.util

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import ru.resodostudios.cashsense.core.util.Constants.DEEPLINK_TAG_SUBSCRIPTIONS
import ru.resodostudios.cashsense.feature.home.api.HomeNavKey
import ru.resodostudios.cashsense.feature.subscription.list.api.SubscriptionsNavKey

internal fun Uri?.toKey(): NavKey {
    if (this == null) return HomeNavKey()

    val paths = pathSegments

    if (pathSegments.isEmpty()) return HomeNavKey()

    return when(paths.first()) {
        DEEPLINK_TAG_SUBSCRIPTIONS -> SubscriptionsNavKey
        else -> HomeNavKey()
    }
}