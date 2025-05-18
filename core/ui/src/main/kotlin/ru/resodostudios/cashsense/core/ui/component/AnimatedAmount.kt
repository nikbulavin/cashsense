package ru.resodostudios.cashsense.core.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AnimatedAmount(
    targetState: BigDecimal,
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedContentScope.(targetState: BigDecimal) -> Unit,
) {
    val animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec<IntOffset>()
    val fadeAnimationSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            if (targetState > initialState) {
                (slideInVertically(animationSpec) { -it } + fadeIn())
                    .togetherWith(slideOutVertically { it } + fadeOut(fadeAnimationSpec))
            } else {
                (slideInVertically(animationSpec) { it } + fadeIn())
                    .togetherWith(slideOutVertically { -it } + fadeOut(fadeAnimationSpec))
            }.using(SizeTransform(clip = false))
        },
        label = label,
        modifier = modifier,
        content = content,
    )
}