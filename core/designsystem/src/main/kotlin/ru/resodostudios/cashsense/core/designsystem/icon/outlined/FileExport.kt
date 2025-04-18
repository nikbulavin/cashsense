package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.FileExport: ImageVector
    get() {
        if (_FileExport != null) {
            return _FileExport!!
        }
        _FileExport = ImageVector.Builder(
            name = "Outlined.FileExport",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(480f, 480f)
                lineTo(480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                lineTo(480f, 480f)
                lineTo(480f, 480f)
                lineTo(480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                lineTo(480f, 480f)
                lineTo(480f, 480f)
                close()
                moveTo(202f, 895f)
                lineTo(146f, 838f)
                lineTo(264f, 720f)
                lineTo(174f, 720f)
                lineTo(174f, 640f)
                lineTo(400f, 640f)
                lineTo(400f, 866f)
                lineTo(320f, 866f)
                lineTo(320f, 777f)
                lineTo(202f, 895f)
                close()
                moveTo(480f, 880f)
                lineTo(480f, 800f)
                lineTo(720f, 800f)
                quadTo(720f, 800f, 720f, 800f)
                quadTo(720f, 800f, 720f, 800f)
                lineTo(720f, 360f)
                lineTo(520f, 360f)
                lineTo(520f, 160f)
                lineTo(240f, 160f)
                quadTo(240f, 160f, 240f, 160f)
                quadTo(240f, 160f, 240f, 160f)
                lineTo(240f, 560f)
                lineTo(160f, 560f)
                lineTo(160f, 160f)
                quadTo(160f, 127f, 183.5f, 103.5f)
                quadTo(207f, 80f, 240f, 80f)
                lineTo(560f, 80f)
                lineTo(800f, 320f)
                lineTo(800f, 800f)
                quadTo(800f, 833f, 776.5f, 856.5f)
                quadTo(753f, 880f, 720f, 880f)
                lineTo(480f, 880f)
                close()
            }
        }.build()

        return _FileExport!!
    }

@Suppress("ObjectPropertyName")
private var _FileExport: ImageVector? = null
