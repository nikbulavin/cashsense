package ru.resodostudios.cashsense.wallet.widget.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.resodostudios.cashsense.core.model.data.ExtendedWallet
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.util.Constants.DEEP_LINK_SCHEME_AND_HOST
import ru.resodostudios.cashsense.core.util.Constants.HOME_PATH
import ru.resodostudios.cashsense.core.util.Constants.TARGET_ACTIVITY_NAME
import ru.resodostudios.cashsense.core.util.Constants.TRANSACTION_PATH
import ru.resodostudios.cashsense.feature.wallet.widget.R
import ru.resodostudios.cashsense.wallet.widget.WalletWidgetEntryPoint
import ru.resodostudios.cashsense.wallet.widget.ui.theme.CsGlanceTheme
import ru.resodostudios.cashsense.wallet.widget.ui.theme.CsGlanceTypography
import ru.resodostudios.cashsense.core.locales.R as localesR

class WalletWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val walletsEntryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WalletWidgetEntryPoint::class.java,
        )
        val walletsRepository = walletsEntryPoint.walletsRepository()

        val initialWallets = withContext(Dispatchers.IO) {
            walletsRepository.getWalletsWithTransactionsAndCategories()
                .first()
                .sortedByDescending { it.wallet.id }
        }

        provideContent {
            val wallets by walletsRepository.getWalletsWithTransactionsAndCategories()
                .collectAsState(initialWallets)

            CsGlanceTheme {
                WalletWidgetContent(wallets)
            }
        }
    }
}

@Composable
private fun WalletWidgetContent(wallets: List<ExtendedWallet>) {
    val context = LocalContext.current
    Scaffold(
        titleBar = {
            TitleBar(
                startIcon = ImageProvider(R.drawable.ic_outlined_wallet),
                title = context.getString(localesR.string.wallet_widget_title),
                modifier = GlanceModifier.clickable(openHomeScreen(context)),
            )
        },
        modifier = GlanceModifier.cornerRadius(24.dp),
    ) {
        if (wallets.isNotEmpty()) {
            LazyColumn {
                items(
                    items = wallets,
                    itemId = { walletPopulated ->
                        walletPopulated.wallet.id.hashCode().toLong()
                    },
                ) { walletPopulated ->
                    val currentBalance = walletPopulated.transactionsWithCategories
                        .sumOf { it.transaction.amount }
                        .plus(walletPopulated.wallet.initialBalance)

                    Column {
                        WalletItem(
                            context = context,
                            walletId = walletPopulated.wallet.id,
                            title = walletPopulated.wallet.title,
                            currentBalance = currentBalance.formatAmount(walletPopulated.wallet.currency),
                            onClick = openHomeScreen(context, walletPopulated.wallet.id),
                        )
                        Spacer(GlanceModifier.height(4.dp))
                    }
                }
            }
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = GlanceModifier.fillMaxSize(),
            ) {
                Text(
                    text = context.getString(localesR.string.wallet_widget_empty),
                    style = CsGlanceTypography.titleMedium
                        .copy(color = GlanceTheme.colors.onBackground),
                )
            }
        }
    }
}

@Composable
fun WalletItem(
    context: Context,
    walletId: String,
    title: String,
    currentBalance: String,
    onClick: Action,
    modifier: GlanceModifier = GlanceModifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 12.dp, top = 6.dp, bottom = 6.dp, end = 6.dp)
            .cornerRadius(12.dp)
            .background(GlanceTheme.colors.secondaryContainer)
            .clickable(onClick),
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
                    .copy(color = GlanceTheme.colors.onSecondaryContainer),
                maxLines = 1,
            )
        }
        CircleIconButton(
            imageProvider = ImageProvider(R.drawable.ic_outlined_add),
            onClick = actionStartActivity(
                Intent().apply {
                    action = Intent.ACTION_VIEW
                    data =
                        "$DEEP_LINK_SCHEME_AND_HOST/$TRANSACTION_PATH/$walletId/null/false".toUri()
                    component = ComponentName(context.packageName, TARGET_ACTIVITY_NAME)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
            ),
            contentDescription = context.getString(localesR.string.add),
            backgroundColor = null,
            contentColor = GlanceTheme.colors.onSecondaryContainer,
        )
    }
}

private fun openHomeScreen(
    context: Context,
    walletId: String? = null,
): Action {
    return actionStartActivity(
        Intent().apply {
            action = Intent.ACTION_VIEW
            data = "$DEEP_LINK_SCHEME_AND_HOST/$HOME_PATH/$walletId".toUri()
            component = ComponentName(context.packageName, TARGET_ACTIVITY_NAME)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    )
}