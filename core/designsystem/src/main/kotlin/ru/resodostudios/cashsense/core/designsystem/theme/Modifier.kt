package ru.resodostudios.cashsense.core.designsystem.theme

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.PlaceholderSize.Companion.ContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope

@Composable
fun Modifier.dropShadow(shape: Shape): Modifier {
    return dropShadow(
        shape = shape,
        shadow = Shadow(
            radius = 6.dp,
            color = MaterialTheme.colorScheme.inverseSurface.copy(0.2f),
        ),
    )
}

@Composable
fun Modifier.sharedBoundsWithDefaults(
    sharedContentState: SharedTransitionScope.SharedContentState,
    sharedTransitionScope: SharedTransitionScope = LocalSharedTransitionScope.current,
    animatedVisibilityScope: AnimatedVisibilityScope = LocalNavAnimatedContentScope.current,
    boundsTransform: BoundsTransform = MaterialTheme.motionScheme.sharedElementTransitionSpec,
    resizeMode: SharedTransitionScope.ResizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
    clipShape: Shape = MaterialTheme.shapes.large,
    renderInOverlayDuringTransition: Boolean = true,
    placeholderSize: SharedTransitionScope.PlaceholderSize = ContentSize,
): Modifier {
    with(sharedTransitionScope) {
        val motionScheme = MaterialTheme.motionScheme
        return this@sharedBoundsWithDefaults
            .sharedBounds(
                sharedContentState = sharedContentState,
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = boundsTransform,
                resizeMode = resizeMode,
                clipInOverlayDuringTransition = OverlayClip(clipShape),
                renderInOverlayDuringTransition = renderInOverlayDuringTransition,
                placeholderSize = placeholderSize,
                exit = fadeOut(motionScheme.defaultEffectsSpec()),
                enter = fadeIn(motionScheme.defaultEffectsSpec()),
            )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun Modifier.sharedBoundsAdaptive(
    sharedContentState: SharedTransitionScope.SharedContentState,
    sharedTransitionScope: SharedTransitionScope = LocalSharedTransitionScope.current,
    animatedVisibilityScope: AnimatedVisibilityScope = LocalNavAnimatedContentScope.current,
    boundsTransform: BoundsTransform = MaterialTheme.motionScheme.sharedElementTransitionSpec,
    resizeMode: SharedTransitionScope.ResizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
    clipShape: Shape = MaterialTheme.shapes.large,
    renderInOverlayDuringTransition: Boolean = true,
    placeholderSize: SharedTransitionScope.PlaceholderSize = ContentSize,
): Modifier {
    val directive = calculatePaneScaffoldDirective(currentWindowAdaptiveInfoV2())
    val isMultiPane = directive.maxHorizontalPartitions > 1

    if (isMultiPane) return this

    return this@sharedBoundsAdaptive.sharedBoundsWithDefaults(
        sharedContentState = sharedContentState,
        animatedVisibilityScope = animatedVisibilityScope,
        sharedTransitionScope = sharedTransitionScope,
        boundsTransform = boundsTransform,
        resizeMode = resizeMode,
        clipShape = clipShape,
        renderInOverlayDuringTransition = renderInOverlayDuringTransition,
        placeholderSize = placeholderSize,
    )
}