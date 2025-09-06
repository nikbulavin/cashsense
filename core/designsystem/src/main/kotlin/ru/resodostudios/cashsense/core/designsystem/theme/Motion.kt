package ru.resodostudios.cashsense.core.designsystem.theme

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalSharedTransitionApi::class,
)
val MotionScheme.sharedElementTransitionSpec: BoundsTransform
    @Composable
    get() = object : BoundsTransform {
        override fun transform(
            initialBounds: Rect,
            targetBounds: Rect,
        ): FiniteAnimationSpec<Rect> {
            return this@sharedElementTransitionSpec.slowSpatialSpec()
        }
    }
