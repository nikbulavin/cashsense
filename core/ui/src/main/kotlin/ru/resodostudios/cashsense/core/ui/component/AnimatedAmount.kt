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
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import java.math.BigDecimal
import java.util.Currency

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AnimatedAmount(
    amount: BigDecimal,
    currency: Currency,
    label: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    withApproximatelySign: Boolean = false,
) {
    val animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec<IntOffset>()
    val fadeAnimationSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    AnimatedContent(
        targetState = amount,
        transitionSpec = {
            if (amount > initialState) {
                (slideInVertically(animationSpec) { -it } + fadeIn())
                    .togetherWith(slideOutVertically { it } + fadeOut(fadeAnimationSpec))
            } else {
                (slideInVertically(animationSpec) { it } + fadeIn())
                    .togetherWith(slideOutVertically { -it } + fadeOut(fadeAnimationSpec))
            }
        },
        label = label,
        modifier = modifier,
        content = {
            Text(
                text = it.formatAmount(
                    currency = currency,
                    withApproximately = withApproximatelySign,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = style,
                color = color,
            )
        },
    )
}