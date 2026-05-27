package ru.resodostudios.cashsense.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.designsystem.theme.LocalSharedTransitionScope

class TransitionThemeWrapper : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) {
        CsTheme {
            SharedTransitionLayout {
                AnimatedContent(true) {
                    if (it) {
                        CompositionLocalProvider(
                            LocalSharedTransitionScope provides this@SharedTransitionLayout,
                            LocalNavAnimatedContentScope provides this,
                        ) {
                            Surface {
                                content()
                            }
                        }
                    }
                }
            }
        }
    }
}