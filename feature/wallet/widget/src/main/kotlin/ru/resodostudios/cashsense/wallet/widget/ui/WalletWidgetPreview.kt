package ru.resodostudios.cashsense.wallet.widget.ui

import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.os.Build
import androidx.collection.intSetOf
import androidx.glance.appwidget.GlanceAppWidgetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.wallet.widget.WalletWidgetReceiver

fun updateWalletWidgetPreview(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        CoroutineScope(Dispatchers.IO).launch {
            val glanceAppWidgetManager = GlanceAppWidgetManager(context)
            glanceAppWidgetManager.setWidgetPreviews(
                WalletWidgetReceiver::class,
                intSetOf(AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN),
            )
        }
    }
}