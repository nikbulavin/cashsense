package ru.resodostudios.cashsense.feature.home.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.resodostudios.cashsense.core.ui.component.MessageWithAnimation
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun HomeDetailPlaceholder(
    modifier: Modifier = Modifier,
) {
    MessageWithAnimation(
        messageRes = localesR.string.select_wallet,
        animationRes = R.raw.anim_select_wallet,
        modifier = modifier,
    )
}