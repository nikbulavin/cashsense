package ru.resodostudios.cashsense.feature.transaction.dialog.impl.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import androidx.navigation.navDeepLink
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.core.util.Constants.DEEP_LINK_SCHEME_AND_HOST
import ru.resodostudios.cashsense.core.util.Constants.REPEATED_KEY
import ru.resodostudios.cashsense.core.util.Constants.TRANSACTION_ID_KEY
import ru.resodostudios.cashsense.core.util.Constants.TRANSACTION_PATH
import ru.resodostudios.cashsense.core.util.Constants.WALLET_ID_KEY
import ru.resodostudios.cashsense.feature.transaction.dialog.impl.TransactionDialog

private const val DEEP_LINK_URI_PATTERN =
    "$DEEP_LINK_SCHEME_AND_HOST/$TRANSACTION_PATH/{$WALLET_ID_KEY}/{$TRANSACTION_ID_KEY}/{$REPEATED_KEY}"

@Serializable
data class TransactionDialogRoute(
    val walletId: String,
    val transactionId: String? = null,
    val repeated: Boolean = false,
)

fun NavController.navigateToTransactionDialog(
    walletId: String,
    transactionId: String? = null,
    repeated: Boolean = false,
) {
    navigate(route = TransactionDialogRoute(walletId, transactionId, repeated)) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.transactionDialog(
    onDismiss: () -> Unit,
) {
    dialog<TransactionDialogRoute>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DEEP_LINK_URI_PATTERN
            },
        ),
    ) {
        TransactionDialog(
            onDismiss = onDismiss,
        )
    }
}