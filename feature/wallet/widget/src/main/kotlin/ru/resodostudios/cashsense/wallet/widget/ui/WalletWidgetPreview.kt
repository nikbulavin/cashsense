package ru.resodostudios.cashsense.wallet.widget.ui

import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.os.Build
import androidx.collection.intSetOf
import androidx.glance.appwidget.GlanceAppWidgetManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.resodostudios.cashsense.wallet.widget.WalletWidgetReceiver

suspend fun updateWalletWidgetPreview(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        withContext(Dispatchers.IO) {
            runCatching {
                GlanceAppWidgetManager(context).setWidgetPreviews(
                    WalletWidgetReceiver::class,
                    intSetOf(AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN),
                )
            }
        }
    }
}
