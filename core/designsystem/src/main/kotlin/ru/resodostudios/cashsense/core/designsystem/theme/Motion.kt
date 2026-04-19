package ru.resodostudios.cashsense.core.designsystem.theme

import androidx.compose.animation.BoundsTransform
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable

val MotionScheme.sharedElementTransitionSpec: BoundsTransform
    @Composable get() = BoundsTransform { _, _ -> this@sharedElementTransitionSpec.defaultSpatialSpec() }