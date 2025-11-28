package ru.resodostudios.cashsense.core.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AnimatedAmount(
    formattedAmount: String,
    label: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
) {
    val slideAnimSpec = MaterialTheme.motionScheme.defaultSpatialSpec<IntOffset>()
    val fadeAnimSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    AnimatedContent(
        targetState = formattedAmount,
        transitionSpec = {
            slideInVertically(slideAnimSpec) { -it } + fadeIn(fadeAnimSpec) togetherWith
                    slideOutVertically { it } + fadeOut(fadeAnimSpec)
        },
        label = label,
        modifier = modifier,
        content = {
            Text(
                text = it,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = style,
                color = color,
            )
        },
    )
}