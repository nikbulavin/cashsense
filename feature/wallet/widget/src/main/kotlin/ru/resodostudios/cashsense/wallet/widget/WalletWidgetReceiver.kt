package ru.resodostudios.cashsense.wallet.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import ru.resodostudios.cashsense.wallet.widget.ui.WalletWidget

internal class WalletWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = WalletWidget()
}