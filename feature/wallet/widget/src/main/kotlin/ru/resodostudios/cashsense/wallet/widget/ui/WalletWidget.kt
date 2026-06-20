package ru.resodostudios.cashsense.wallet.widget.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.PreviewSizeMode
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import dagger.hilt.android.EntryPointAccessors
import ru.resodostudios.cashsense.core.common.Constants.DEEPLINK_PATH_BASE
import ru.resodostudios.cashsense.core.common.Constants.DEEPLINK_TAG_HOME
import ru.resodostudios.cashsense.core.common.Constants.DEEPLINK_TAG_TRANSACTION
import ru.resodostudios.cashsense.core.common.Constants.DEEPLINK_TAG_WALLET
import ru.resodostudios.cashsense.core.common.Constants.TARGET_ACTIVITY_NAME
import ru.resodostudios.cashsense.core.common.getUsdCurrency
import ru.resodostudios.cashsense.core.model.ExtendedUserWallet
import ru.resodostudios.cashsense.core.model.Wallet
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.feature.wallet.widget.R
import ru.resodostudios.cashsense.wallet.widget.WalletWidgetEntryPoint
import ru.resodostudios.cashsense.wallet.widget.ui.theme.CsGlanceTheme
import ru.resodostudios.cashsense.wallet.widget.ui.theme.CsGlanceTypography
import java.math.BigDecimal
import java.util.Currency
import ru.resodostudios.cashsense.core.locales.R as localesR

internal class WalletWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode
        get() = SizeMode.Exact

    override val previewSizeMode: PreviewSizeMode
        get() = SizeMode.Responsive(setOf(DpSize(180.dp, 110.dp)))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val walletsEntryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WalletWidgetEntryPoint::class.java,
        )
        val getExtendedUserWalletsUseCase = walletsEntryPoint.getExtendedUserWalletsUseCase()

        provideContent {
            val extendedWallets by getExtendedUserWalletsUseCase().collectAsState(emptyList())

            CsGlanceTheme {
                WalletWidgetContent(
                    extendedWallets = extendedWallets,
                )
            }
        }
    }

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        provideContent {
            CsGlanceTheme {
                WalletWidgetContent(
                    extendedWallets = listOf(
                        ExtendedUserWallet(
                            wallet = Wallet(
                                id = "1",
                                title = "Credit",
                                initialBalance = BigDecimal.ZERO,
                                currency = getUsdCurrency(),
                            ),
                            transactions = emptyList(),
                            currentBalance = BigDecimal(15000),
                            isPrimary = true,
                        ),
                        ExtendedUserWallet(
                            wallet = Wallet(
                                id = "2",
                                title = "Debit",
                                initialBalance = BigDecimal.ZERO,
                                currency = Currency.getInstance("EUR"),
                            ),
                            transactions = emptyList(),
                            currentBalance = BigDecimal(547.2),
                            isPrimary = false,
                        ),
                    ),
                )
            }
        }
    }
}

@Composable
private fun WalletWidgetContent(
    extendedWallets: List<ExtendedUserWallet>,
) {
    val context = LocalContext.current
    Scaffold(
        titleBar = {
            TitleBar(
                startIcon = ImageProvider(R.drawable.feature_wallet_widget_ic_wallet),
                title = context.getString(localesR.string.wallet_widget_title),
                modifier = GlanceModifier.clickable(
                    actionStartActivity(
                        Intent().apply {
                            action = Intent.ACTION_VIEW
                            data = "$DEEPLINK_PATH_BASE/$DEEPLINK_TAG_HOME".toUri()
                            component = ComponentName(context.packageName, TARGET_ACTIVITY_NAME)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        },
                    ),
                ),
            )
        },
        modifier = GlanceModifier.cornerRadius(24.dp),
    ) {
        if (extendedWallets.isNotEmpty()) {
            LazyColumn {
                items(
                    items = extendedWallets,
                    itemId = { it.wallet.id.hashCode().toLong() },
                ) { extendedWallet ->
                    Column {
                        WalletItem(
                            context = context,
                            walletId = extendedWallet.wallet.id,
                            title = extendedWallet.wallet.title,
                            currentBalance = extendedWallet.currentBalance.formatAmount(
                                extendedWallet.wallet.currency,
                            ),
                        )
                        Spacer(GlanceModifier.height(4.dp))
                    }
                }
                item { Spacer(GlanceModifier.height(8.dp)) }
            }
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = GlanceModifier.fillMaxSize(),
            ) {
                Text(
                    text = context.getString(localesR.string.wallet_widget_empty),
                    style = CsGlanceTypography.titleMedium.copy(color = GlanceTheme.colors.onBackground),
                )
            }
        }
    }
}

@Composable
private fun WalletItem(
    context: Context,
    walletId: String,
    title: String,
    currentBalance: String,
    modifier: GlanceModifier = GlanceModifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 12.dp, top = 6.dp, bottom = 6.dp, end = 6.dp)
            .cornerRadius(16.dp)
            .background(GlanceTheme.colors.secondaryContainer)
            .clickable(
                actionStartActivity(
                    Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = "$DEEPLINK_PATH_BASE/$DEEPLINK_TAG_WALLET/$walletId".toUri()
                        component = ComponentName(context.packageName, TARGET_ACTIVITY_NAME)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    },
                ),
            ),
    ) {
        Column(
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.Start,
            modifier = GlanceModifier.defaultWeight(),
        ) {
            Text(
                text = title,
                style = CsGlanceTypography.titleMedium
                    .copy(color = GlanceTheme.colors.onSecondaryContainer),
                maxLines = 1,
            )
            Text(
                text = currentBalance,
                style = CsGlanceTypography.bodyMedium
                    .copy(color = GlanceTheme.colors.onSurfaceVariant),
                maxLines = 1,
            )
        }
        CircleIconButton(
            imageProvider = ImageProvider(R.drawable.feature_wallet_widget_ic_add),
            onClick = actionStartActivity(
                Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = "$DEEPLINK_PATH_BASE/$DEEPLINK_TAG_TRANSACTION/$walletId".toUri()
                    component = ComponentName(context.packageName, TARGET_ACTIVITY_NAME)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                },
            ),
            contentDescription = context.getString(localesR.string.add),
            backgroundColor = null,
            contentColor = GlanceTheme.colors.onSecondaryContainer,
        )
    }
}