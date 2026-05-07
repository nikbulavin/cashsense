package ru.resodostudios.cashsense.core.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.snap
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
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.hazeEffect

@OptIn(ExperimentalHazeApi::class)
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
        val initialAmount = remember(targetAmount) { previousAmount }
        val isEntering = targetAmount == formattedAmount

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            targetAmount.forEachIndexed { index, char ->
                val shouldAnimate = if (isEntering) {
                    index >= initialAmount.length || char != initialAmount[index]
                } else {
                    index >= formattedAmount.length || char != formattedAmount[index]
                }
                val animDuration = 400
                val animDelay = index * 50

                val animatedBlurRadius by transition.animateDp(
                    transitionSpec = {
                        if (shouldAnimate) tween(animDuration, animDelay) else snap()
                    },
                    label = "BlurChar_$index",
                ) { state ->
                    if (shouldAnimate && state != EnterExitState.Visible) 10.dp else 0.dp
                }

                Text(
                    text = char.toString(),
                    modifier = Modifier
                        .hazeEffect {
                            blurRadius = animatedBlurRadius
                            blurEnabled = animatedBlurRadius > 0.dp
                            noiseFactor = 0f
                            inputScale = HazeInputScale.Auto
                        }
                        .animateEnterExit(
                            enter = if (shouldAnimate) {
                                slideInVertically(tween(animDuration, animDelay)) { -it / 2 } +
                                        fadeIn(tween(animDuration, animDelay))
                            } else {
                                fadeIn(snap())
                            },
                            exit = if (shouldAnimate) {
                                slideOutVertically(tween(animDuration, animDelay)) { it / 2 } +
                                        fadeOut(tween(animDuration, animDelay))
                            } else {
                                fadeOut(snap())
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
