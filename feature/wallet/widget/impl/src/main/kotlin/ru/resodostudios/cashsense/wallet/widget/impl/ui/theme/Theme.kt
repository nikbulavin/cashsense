package ru.resodostudios.cashsense.wallet.widget.impl.ui.theme

import androidx.compose.runtime.Composable
import androidx.glance.GlanceTheme
import ru.resodostudios.cashsense.core.designsystem.theme.supportsDynamicTheming

@Composable
internal fun CsGlanceTheme(
    content: @Composable () -> Unit,
) {
    GlanceTheme(
        colors = if (supportsDynamicTheming()) GlanceTheme.colors else CsGlanceColorScheme.colors,
        content = content,
    )
}