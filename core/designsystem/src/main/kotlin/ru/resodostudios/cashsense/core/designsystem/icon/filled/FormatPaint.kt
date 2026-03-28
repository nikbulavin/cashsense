package ru.resodostudios.cashsense.core.designsystem.icon.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Filled.FormatPaint: ImageVector
    get() {
        if (_FormatPaint != null) {
            return _FormatPaint!!
        }
        _FormatPaint = ImageVector.Builder(
            name = "Filled.FormatPaint",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(440f, 880f)
                quadTo(407f, 880f, 383.5f, 856.5f)
                quadTo(360f, 833f, 360f, 800f)
                lineTo(360f, 640f)
                lineTo(240f, 640f)
                quadTo(207f, 640f, 183.5f, 616.5f)
                quadTo(160f, 593f, 160f, 560f)
                lineTo(160f, 280f)
                quadTo(160f, 214f, 207f, 167f)
                quadTo(254f, 120f, 320f, 120f)
                lineTo(800f, 120f)
                lineTo(800f, 560f)
                quadTo(800f, 593f, 776.5f, 616.5f)
                quadTo(753f, 640f, 720f, 640f)
                lineTo(600f, 640f)
                lineTo(600f, 800f)
                quadTo(600f, 833f, 576.5f, 856.5f)
                quadTo(553f, 880f, 520f, 880f)
                lineTo(440f, 880f)
                close()
                moveTo(240f, 400f)
                lineTo(720f, 400f)
                lineTo(720f, 200f)
                lineTo(680f, 200f)
                lineTo(680f, 360f)
                lineTo(600f, 360f)
                lineTo(600f, 200f)
                lineTo(560f, 200f)
                lineTo(560f, 280f)
                lineTo(480f, 280f)
                lineTo(480f, 200f)
                lineTo(320f, 200f)
                quadTo(287f, 200f, 263.5f, 223.5f)
                quadTo(240f, 247f, 240f, 280f)
                lineTo(240f, 400f)
                close()
            }
        }.build()

        return _FormatPaint!!
    }

@Suppress("ObjectPropertyName")
private var _FormatPaint: ImageVector? = null
