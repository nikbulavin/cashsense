package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.Android: ImageVector
    get() {
        if (_Android != null) {
            return _Android!!
        }
        _Android = ImageVector.Builder(
            name = "Outlined.Android",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(40f, 720f)
                quadTo(49f, 613f, 105.5f, 523f)
                quadTo(162f, 433f, 256f, 380f)
                lineTo(182f, 252f)
                quadTo(176f, 243f, 179f, 233f)
                quadTo(182f, 223f, 192f, 218f)
                quadTo(200f, 213f, 210f, 216f)
                quadTo(220f, 219f, 226f, 228f)
                lineTo(300f, 356f)
                quadTo(386f, 320f, 480f, 320f)
                quadTo(574f, 320f, 660f, 356f)
                lineTo(734f, 228f)
                quadTo(740f, 219f, 750f, 216f)
                quadTo(760f, 213f, 768f, 218f)
                quadTo(778f, 223f, 781f, 233f)
                quadTo(784f, 243f, 778f, 252f)
                lineTo(704f, 380f)
                quadTo(798f, 433f, 854.5f, 523f)
                quadTo(911f, 613f, 920f, 720f)
                lineTo(40f, 720f)
                close()
                moveTo(280f, 610f)
                quadTo(301f, 610f, 315.5f, 595.5f)
                quadTo(330f, 581f, 330f, 560f)
                quadTo(330f, 539f, 315.5f, 524.5f)
                quadTo(301f, 510f, 280f, 510f)
                quadTo(259f, 510f, 244.5f, 524.5f)
                quadTo(230f, 539f, 230f, 560f)
                quadTo(230f, 581f, 244.5f, 595.5f)
                quadTo(259f, 610f, 280f, 610f)
                close()
                moveTo(680f, 610f)
                quadTo(701f, 610f, 715.5f, 595.5f)
                quadTo(730f, 581f, 730f, 560f)
                quadTo(730f, 539f, 715.5f, 524.5f)
                quadTo(701f, 510f, 680f, 510f)
                quadTo(659f, 510f, 644.5f, 524.5f)
                quadTo(630f, 539f, 630f, 560f)
                quadTo(630f, 581f, 644.5f, 595.5f)
                quadTo(659f, 610f, 680f, 610f)
                close()
            }
        }.build()

        return _Android!!
    }

@Suppress("ObjectPropertyName")
private var _Android: ImageVector? = null
