package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.SentimentSad: ImageVector
    get() {
        if (_SentimentSad != null) {
            return _SentimentSad!!
        }
        _SentimentSad = ImageVector.Builder(
            name = "Outlined.SentimentSad",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(250f, 640f)
                lineTo(310f, 640f)
                lineTo(310f, 630f)
                quadTo(310f, 559f, 359.5f, 509.5f)
                quadTo(409f, 460f, 480f, 460f)
                quadTo(551f, 460f, 600.5f, 509.5f)
                quadTo(650f, 559f, 650f, 630f)
                lineTo(650f, 640f)
                lineTo(710f, 640f)
                lineTo(710f, 630f)
                quadTo(710f, 534f, 643f, 467f)
                quadTo(576f, 400f, 480f, 400f)
                quadTo(384f, 400f, 317f, 467f)
                quadTo(250f, 534f, 250f, 630f)
                lineTo(250f, 640f)
                close()
                moveTo(284f, 370f)
                quadTo(325f, 364f, 370.5f, 338f)
                quadTo(416f, 312f, 443f, 279f)
                lineTo(397f, 241f)
                quadTo(377f, 265f, 341.5f, 285f)
                quadTo(306f, 305f, 276f, 310f)
                lineTo(284f, 370f)
                close()
                moveTo(676f, 370f)
                lineTo(684f, 310f)
                quadTo(654f, 305f, 618.5f, 285f)
                quadTo(583f, 265f, 563f, 241f)
                lineTo(517f, 279f)
                quadTo(544f, 312f, 589.5f, 338f)
                quadTo(635f, 364f, 676f, 370f)
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

        return _SentimentSad!!
    }

@Suppress("ObjectPropertyName")
private var _SentimentSad: ImageVector? = null
