package ru.resodostudios.cashsense.util

import androidx.core.net.toUri
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import kotlinx.serialization.serializer
import ru.resodostudios.cashsense.core.common.Constants.DEEPLINK_PATH_BASE
import ru.resodostudios.cashsense.core.common.Constants.DEEPLINK_TAG_HOME
import ru.resodostudios.cashsense.core.common.Constants.DEEPLINK_TAG_SUBSCRIPTIONS
import ru.resodostudios.cashsense.core.common.Constants.DEEPLINK_TAG_TRANSACTION
import ru.resodostudios.cashsense.core.common.Constants.DEEPLINK_TAG_WALLET
import ru.resodostudios.cashsense.feature.home.api.HomeNavKey
import ru.resodostudios.cashsense.feature.subscription.list.api.SubscriptionsNavKey
import ru.resodostudios.cashsense.feature.transaction.editor.api.TransactionEditorNavKey
import ru.resodostudios.cashsense.feature.wallet.detail.api.WalletNavKey
import ru.resodostudios.core.navigation.NavDeepLinkKey

/**
 * Builds a navigation back stack for the given [startKey] by traversing its parent hierarchy.
 *
 * This function uses the `parent` property of [NavDeepLinkKey] to trace the path from the
 * specified destination back to the root. The resulting list is ordered from the
 * top-level parent to the [startKey].
 *
 * @param startKey The destination key to build the back stack from.
 * @return A list of [NavKey] representing the complete navigation path.
 */
fun buildBackStack(startKey: NavKey): List<NavKey> {
    return generateSequence(startKey) { node ->
        (node as? NavDeepLinkKey)?.parent
    }
        .toList()
        .asReversed()
}

private val DeepLinkMatchers = listOf(
    UriDeepLinkMatcher(
        uriPattern = "$DEEPLINK_PATH_BASE/$DEEPLINK_TAG_HOME?walletId={walletId}".toUri(),
        serializer = serializer<HomeNavKey>(),
    ),
    UriDeepLinkMatcher(
        uriPattern = "$DEEPLINK_PATH_BASE/$DEEPLINK_TAG_SUBSCRIPTIONS".toUri(),
        serializer = serializer<SubscriptionsNavKey>(),
    ),
    UriDeepLinkMatcher(
        uriPattern = "$DEEPLINK_PATH_BASE/$DEEPLINK_TAG_TRANSACTION/{walletId}".toUri(),
        serializer = serializer<TransactionEditorNavKey>(),
    ),
    UriDeepLinkMatcher(
        uriPattern = "$DEEPLINK_PATH_BASE/$DEEPLINK_TAG_WALLET/{walletId}".toUri(),
        serializer = serializer<WalletNavKey>(),
    ),
)

fun DeepLinkRequest?.toKey(): NavKey {
    if (this == null) return HomeNavKey()
    return DeepLinkMatchers.firstNotNullOfOrNull { matcher ->
        runCatching { matcher.match(this)?.key }.getOrNull()
    } ?: HomeNavKey()
}
