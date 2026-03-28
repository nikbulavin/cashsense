package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.MusicNote: ImageVector
    get() {
        if (_MusicNote != null) {
            return _MusicNote!!
        }
        _MusicNote = ImageVector.Builder(
            name = "Outlined.MusicNote",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(127f, 793f)
                quadTo(80f, 746f, 80f, 680f)
                quadTo(80f, 614f, 127f, 567f)
                quadTo(174f, 520f, 240f, 520f)
                quadTo(263f, 520f, 282.5f, 525.5f)
                quadTo(302f, 531f, 320f, 542f)
                lineTo(320f, 200f)
                lineTo(800f, 120f)
                lineTo(800f, 600f)
                quadTo(800f, 666f, 753f, 713f)
                quadTo(706f, 760f, 640f, 760f)
                quadTo(574f, 760f, 527f, 713f)
                quadTo(480f, 666f, 480f, 600f)
                quadTo(480f, 534f, 527f, 487f)
                quadTo(574f, 440f, 640f, 440f)
                quadTo(663f, 440f, 682.5f, 445.5f)
                quadTo(702f, 451f, 720f, 462f)
                lineTo(720f, 297f)
                lineTo(400f, 360f)
                lineTo(400f, 680f)
                quadTo(400f, 746f, 353f, 793f)
                quadTo(306f, 840f, 240f, 840f)
                quadTo(174f, 840f, 127f, 793f)
                close()
            }
        }.build()

        return _MusicNote!!
    }

@Suppress("ObjectPropertyName")
private var _MusicNote: ImageVector? = null
