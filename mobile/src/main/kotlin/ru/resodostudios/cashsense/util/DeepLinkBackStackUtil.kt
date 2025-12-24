package ru.resodostudios.cashsense.util

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import ru.resodostudios.cashsense.core.util.Constants.DEEPLINK_TAG_HOME
import ru.resodostudios.cashsense.core.util.Constants.DEEPLINK_TAG_WALLET
import ru.resodostudios.cashsense.core.util.Constants.DEEPLINK_TAG_SUBSCRIPTIONS
import ru.resodostudios.cashsense.core.util.Constants.DEEPLINK_TAG_TRANSACTION
import ru.resodostudios.cashsense.feature.home.api.HomeNavKey
import ru.resodostudios.cashsense.feature.subscription.list.api.SubscriptionsNavKey
import ru.resodostudios.cashsense.feature.transaction.dialog.api.TransactionDialogNavKey
import ru.resodostudios.cashsense.feature.wallet.detail.api.WalletNavKey
import ru.resodostudios.core.navigation.NavDeepLinkKey

fun buildBackStack(
    startKey: NavKey,
): List<NavKey> {
    /**
     * iterate up the parents of the startKey until it reaches the root key (a key without a parent)
     */
    return buildList {
        var node: NavKey? = startKey
        while (node != null) {
            add(0, node)
            val parent = if (node is NavDeepLinkKey) {
                node.parent
            } else null
            node = parent
        }
    }
}

fun Uri?.toKey(): NavKey {
    if (this == null) return HomeNavKey()

    val paths = pathSegments

    if (pathSegments.isEmpty()) return HomeNavKey()

    return when (paths.first()) {
        DEEPLINK_TAG_HOME -> HomeNavKey()
        DEEPLINK_TAG_SUBSCRIPTIONS -> SubscriptionsNavKey
        DEEPLINK_TAG_TRANSACTION -> {
            val walletId = pathSegments[1]
            if (walletId != null) TransactionDialogNavKey(walletId) else HomeNavKey()
        }

        DEEPLINK_TAG_WALLET -> {
            val walletId = pathSegments[1]
            if (walletId != null) WalletNavKey(walletId) else HomeNavKey()
        }

        else -> HomeNavKey()
    }
}