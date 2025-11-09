package ru.resodostudios.cashsense.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.dp

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