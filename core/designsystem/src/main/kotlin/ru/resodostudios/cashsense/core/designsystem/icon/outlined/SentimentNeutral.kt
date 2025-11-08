package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.SentimentNeutral: ImageVector
    get() {
        if (_SentimentNeutral != null) {
            return _SentimentNeutral!!
        }
        _SentimentNeutral = ImageVector.Builder(
            name = "Outlined.SentimentNeutral",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(620f, 440f)
                quadTo(645f, 440f, 662.5f, 422.5f)
                quadTo(680f, 405f, 680f, 380f)
                quadTo(680f, 355f, 662.5f, 337.5f)
                quadTo(645f, 320f, 620f, 320f)
                quadTo(595f, 320f, 577.5f, 337.5f)
                quadTo(560f, 355f, 560f, 380f)
                quadTo(560f, 405f, 577.5f, 422.5f)
                quadTo(595f, 440f, 620f, 440f)
                close()
                moveTo(340f, 440f)
                quadTo(365f, 440f, 382.5f, 422.5f)
                quadTo(400f, 405f, 400f, 380f)
                quadTo(400f, 355f, 382.5f, 337.5f)
                quadTo(365f, 320f, 340f, 320f)
                quadTo(315f, 320f, 297.5f, 337.5f)
                quadTo(280f, 355f, 280f, 380f)
                quadTo(280f, 405f, 297.5f, 422.5f)
                quadTo(315f, 440f, 340f, 440f)
                close()
                moveTo(360f, 620f)
                lineTo(600f, 620f)
                lineTo(600f, 560f)
                lineTo(360f, 560f)
                lineTo(360f, 620f)
                close()
                moveTo(480f, 880f)
                quadTo(397f, 880f, 324f, 848.5f)
                quadTo(251f, 817f, 197f, 763f)
                quadTo(143f, 709f, 111.5f, 636f)
                quadTo(80f, 563f, 80f, 480f)
                quadTo(80f, 397f, 111.5f, 324f)
                quadTo(143f, 251f, 197f, 197f)
                quadTo(251f, 143f, 324f, 111.5f)
                quadTo(397f, 80f, 480f, 80f)
                quadTo(563f, 80f, 636f, 111.5f)
                quadTo(709f, 143f, 763f, 197f)
                quadTo(817f, 251f, 848.5f, 324f)
                quadTo(880f, 397f, 880f, 480f)
                quadTo(880f, 563f, 848.5f, 636f)
                quadTo(817f, 709f, 763f, 763f)
                quadTo(709f, 817f, 636f, 848.5f)
                quadTo(563f, 880f, 480f, 880f)
                close()
                moveTo(480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                close()
                moveTo(480f, 800f)
                quadTo(614f, 800f, 707f, 707f)
                quadTo(800f, 614f, 800f, 480f)
                quadTo(800f, 346f, 707f, 253f)
                quadTo(614f, 160f, 480f, 160f)
                quadTo(346f, 160f, 253f, 253f)
                quadTo(160f, 346f, 160f, 480f)
                quadTo(160f, 614f, 253f, 707f)
                quadTo(346f, 800f, 480f, 800f)
                close()
            }
        }.build()

        return _SentimentNeutral!!
    }

@Suppress("ObjectPropertyName")
private var _SentimentNeutral: ImageVector? = null
