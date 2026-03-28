package ru.resodostudios.cashsense.core.designsystem.icon.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Filled.FolderZip: ImageVector
    get() {
        if (_FolderZip != null) {
            return _FolderZip!!
        }
        _FolderZip = ImageVector.Builder(
            name = "Filled.FolderZip",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(160f, 800f)
                quadTo(127f, 800f, 103.5f, 776.5f)
                quadTo(80f, 753f, 80f, 720f)
                lineTo(80f, 240f)
                quadTo(80f, 207f, 103.5f, 183.5f)
                quadTo(127f, 160f, 160f, 160f)
                lineTo(400f, 160f)
                lineTo(480f, 240f)
                lineTo(800f, 240f)
                quadTo(833f, 240f, 856.5f, 263.5f)
                quadTo(880f, 287f, 880f, 320f)
                lineTo(880f, 720f)
                quadTo(880f, 753f, 856.5f, 776.5f)
                quadTo(833f, 800f, 800f, 800f)
                lineTo(160f, 800f)
                close()
                moveTo(560f, 720f)
                lineTo(640f, 720f)
                lineTo(640f, 640f)
                lineTo(720f, 640f)
                lineTo(720f, 560f)
                lineTo(640f, 560f)
                lineTo(640f, 480f)
                lineTo(720f, 480f)
                lineTo(720f, 400f)
                lineTo(640f, 400f)
                lineTo(640f, 320f)
                lineTo(560f, 320f)
                lineTo(560f, 400f)
                lineTo(640f, 400f)
                lineTo(640f, 480f)
                lineTo(560f, 480f)
                lineTo(560f, 560f)
                lineTo(640f, 560f)
                lineTo(640f, 640f)
                lineTo(560f, 640f)
                lineTo(560f, 720f)
                close()
            }
        }.build()

        return _FolderZip!!
    }

@Suppress("ObjectPropertyName")
private var _FolderZip: ImageVector? = null
