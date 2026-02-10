package ru.resodostudios.cashsense.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.designsystem.theme.LocalSharedTransitionScope

@Composable
fun CsThemePreview(content: @Composable () -> Unit) {
    CsTheme {
        SharedTransitionLayout {
            AnimatedContent(true) {
                if (it) {
                    CompositionLocalProvider(
                        LocalSharedTransitionScope provides this@SharedTransitionLayout,
                        LocalNavAnimatedContentScope provides this,
                    ) {
                        content()
                    }
                }
            }
        }
    }
}