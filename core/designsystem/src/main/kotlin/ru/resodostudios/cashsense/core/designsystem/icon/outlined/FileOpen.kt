package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.FileOpen: ImageVector
    get() {
        if (_FileOpen != null) {
            return _FileOpen!!
        }
        _FileOpen = ImageVector.Builder(
            name = "Outlined.FileOpen",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(240f, 880f)
                quadTo(207f, 880f, 183.5f, 856.5f)
                quadTo(160f, 833f, 160f, 800f)
                lineTo(160f, 160f)
                quadTo(160f, 127f, 183.5f, 103.5f)
                quadTo(207f, 80f, 240f, 80f)
                lineTo(560f, 80f)
                lineTo(800f, 320f)
                lineTo(800f, 560f)
                lineTo(720f, 560f)
                lineTo(720f, 360f)
                lineTo(520f, 360f)
                lineTo(520f, 160f)
                lineTo(240f, 160f)
                quadTo(240f, 160f, 240f, 160f)
                quadTo(240f, 160f, 240f, 160f)
                lineTo(240f, 800f)
                quadTo(240f, 800f, 240f, 800f)
                quadTo(240f, 800f, 240f, 800f)
                lineTo(600f, 800f)
                lineTo(600f, 880f)
                lineTo(240f, 880f)
                close()
                moveTo(878f, 895f)
                lineTo(760f, 777f)
                lineTo(760f, 866f)
                lineTo(680f, 866f)
                lineTo(680f, 640f)
                lineTo(906f, 640f)
                lineTo(906f, 720f)
                lineTo(816f, 720f)
                lineTo(934f, 838f)
                lineTo(878f, 895f)
                close()
                moveTo(240f, 800f)
                lineTo(240f, 560f)
                lineTo(240f, 560f)
                lineTo(240f, 360f)
                lineTo(240f, 160f)
                lineTo(240f, 160f)
                quadTo(240f, 160f, 240f, 160f)
                quadTo(240f, 160f, 240f, 160f)
                lineTo(240f, 800f)
                quadTo(240f, 800f, 240f, 800f)
                quadTo(240f, 800f, 240f, 800f)
                close()
            }
        }.build()

        return _FileOpen!!
    }

@Suppress("ObjectPropertyName")
private var _FileOpen: ImageVector? = null
