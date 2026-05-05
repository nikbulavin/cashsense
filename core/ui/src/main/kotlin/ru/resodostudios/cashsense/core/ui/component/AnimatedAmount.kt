package ru.resodostudios.cashsense.core.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun AnimatedAmount(
    formattedAmount: String,
    label: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
) {
    var previousAmount by remember { mutableStateOf(formattedAmount) }

    AnimatedContent(
        targetState = formattedAmount,
        transitionSpec = {
            (EnterTransition.None togetherWith ExitTransition.None).using(
                SizeTransform(clip = false),
            )
        },
        label = label,
        modifier = modifier,
    ) { targetAmount ->
        val oldAmount = previousAmount

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            targetAmount.forEachIndexed { index, char ->
                val isEntering = targetAmount == formattedAmount
                val isExiting = targetAmount == oldAmount

                val shouldAnimate = if (isEntering) {
                    index >= oldAmount.length || char != oldAmount[index]
                } else if (isExiting) {
                    index >= formattedAmount.length || char != formattedAmount[index]
                } else {
                    false
                }

                Text(
                    text = char.toString(),
                    modifier = Modifier.animateEnterExit(
                        enter = if (shouldAnimate) {
                            slideInVertically(
                                animationSpec = tween(
                                    durationMillis = 400,
                                    delayMillis = index * 50,
                                ),
                            ) { -it / 2 } + fadeIn(
                                animationSpec = tween(
                                    durationMillis = 400,
                                    delayMillis = index * 50,
                                ),
                            )
                        } else {
                            EnterTransition.None
                        },
                        exit = if (shouldAnimate) {
                            slideOutVertically(
                                animationSpec = tween(
                                    durationMillis = 400,
                                    delayMillis = index * 50,
                                ),
                            ) { it / 2 } + fadeOut(
                                animationSpec = tween(
                                    durationMillis = 400,
                                    delayMillis = index * 50,
                                ),
                            )
                        } else {
                            ExitTransition.None
                        },
                    ),
                    style = style,
                    color = color,
                    maxLines = 1,
                    overflow = TextOverflow.Visible,
                    softWrap = false,
                )
            }
        }
    }

    SideEffect {
        previousAmount = formattedAmount
    }
}
