package ru.resodostudios.cashsense.core.designsystem.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AnimatedIcon(
    icon: ImageVector?,
    modifier: Modifier = Modifier,
    label: String = "AnimatedIcon",
    contentDescription: String? = null,
    iconSize: Dp = 24.dp,
) {
    val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()

    AnimatedContent(
        targetState = icon,
        label = label,
        transitionSpec = {
            fadeIn(effectsSpec) + scaleIn(spatialSpec, 0.6f) togetherWith
                    fadeOut(effectsSpec) + scaleOut(spatialSpec, 0.6f)
        },
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) { iconToDisplay ->
        iconToDisplay?.let {
            Icon(
                imageVector = it,
                contentDescription = contentDescription,
                modifier = Modifier.size(iconSize),
            )
        }
    }
}