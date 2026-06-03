package ru.resodostudios.cashsense.core.designsystem.component.button

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun CsButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shapes: ButtonShapes = ButtonDefaults.shapes(),
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shapes = shapes,
        enabled = enabled,
    ) {
        Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}