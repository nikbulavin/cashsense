package ru.resodostudios.cashsense.core.shortcuts

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.network.di.ApplicationScope
import ru.resodostudios.cashsense.core.util.Constants.DEEPLINK_PATH_BASE
import ru.resodostudios.cashsense.core.util.Constants.DEEPLINK_TAG_TRANSACTION
import ru.resodostudios.cashsense.core.util.Constants.TARGET_ACTIVITY_NAME
import javax.inject.Inject
import ru.resodostudios.cashsense.core.locales.R as localesR

private const val DYNAMIC_TRANSACTION_SHORTCUT_ID = "dynamic_new_transaction"
private const val DEEP_LINK_BASE_PATH = "$DEEPLINK_PATH_BASE/$DEEPLINK_TAG_TRANSACTION"

internal class DynamicShortcutManager @Inject constructor(
    @ApplicationScope private val appScope: CoroutineScope,
    @ApplicationContext private val context: Context,
    private val walletsRepository: WalletsRepository,
    private val userDataRepository: UserDataRepository,
) : ShortcutManager {

    override fun addTransactionShortcut(walletId: String) {
        val shortcutInfo = ShortcutInfoCompat.Builder(context, DYNAMIC_TRANSACTION_SHORTCUT_ID)
            .setShortLabel(context.getString(localesR.string.new_transaction))
            .setLongLabel(context.getString(localesR.string.transaction_shortcut_long_label))
            .setIcon(IconCompat.createWithResource(context, R.drawable.ic_shortcut_receipt_long))
            .setIntent(
                Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = "$DEEP_LINK_BASE_PATH/$walletId/null/false".toUri()
                    component = ComponentName(
                        context.packageName,
                        TARGET_ACTIVITY_NAME,
                    )
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
            )
            .build()
        ShortcutManagerCompat.pushDynamicShortcut(context, shortcutInfo)
    }

    override fun removeShortcuts() = ShortcutManagerCompat.removeAllDynamicShortcuts(context)

    override fun syncTransactionShortcut() {
        userDataRepository.userData
            .map { it.primaryWalletId }
            .onEach { primaryWalletId ->
                if (primaryWalletId.isBlank()) return@onEach removeShortcuts()
                runCatching { walletsRepository.getExtendedWallet(primaryWalletId).first() }
                    .onSuccess { addTransactionShortcut(primaryWalletId) }
                    .onFailure { removeShortcuts() }
            }
            .launchIn(appScope)
    }
}