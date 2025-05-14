package ru.resodostudios.cashsense.core.designsystem.icon.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Filled.LightMode: ImageVector
    get() {
        if (_LightMode != null) {
            return _LightMode!!
        }
        _LightMode = ImageVector.Builder(
            name = "Filled.LightMode",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(480f, 680f)
                quadTo(397f, 680f, 338.5f, 621.5f)
                quadTo(280f, 563f, 280f, 480f)
                quadTo(280f, 397f, 338.5f, 338.5f)
                quadTo(397f, 280f, 480f, 280f)
                quadTo(563f, 280f, 621.5f, 338.5f)
                quadTo(680f, 397f, 680f, 480f)
                quadTo(680f, 563f, 621.5f, 621.5f)
                quadTo(563f, 680f, 480f, 680f)
                close()
                moveTo(200f, 520f)
                lineTo(40f, 520f)
                lineTo(40f, 440f)
                lineTo(200f, 440f)
                lineTo(200f, 520f)
                close()
                moveTo(920f, 520f)
                lineTo(760f, 520f)
                lineTo(760f, 440f)
                lineTo(920f, 440f)
                lineTo(920f, 520f)
                close()
                moveTo(440f, 200f)
                lineTo(440f, 40f)
                lineTo(520f, 40f)
                lineTo(520f, 200f)
                lineTo(440f, 200f)
                close()
                moveTo(440f, 920f)
                lineTo(440f, 760f)
                lineTo(520f, 760f)
                lineTo(520f, 920f)
                lineTo(440f, 920f)
                close()
                moveTo(256f, 310f)
                lineTo(155f, 213f)
                lineTo(212f, 154f)
                lineTo(308f, 254f)
                lineTo(256f, 310f)
                close()
                moveTo(748f, 806f)
                lineTo(651f, 705f)
                lineTo(704f, 650f)
                lineTo(805f, 747f)
                lineTo(748f, 806f)
                close()
                moveTo(650f, 256f)
                lineTo(747f, 155f)
                lineTo(806f, 212f)
                lineTo(706f, 308f)
                lineTo(650f, 256f)
                close()
                moveTo(154f, 748f)
                lineTo(255f, 651f)
                lineTo(310f, 704f)
                lineTo(213f, 805f)
                lineTo(154f, 748f)
                close()
            }
        }.build()

        return _LightMode!!
    }

@Suppress("ObjectPropertyName")
private var _LightMode: ImageVector? = null
