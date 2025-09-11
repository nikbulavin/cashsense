package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.Description: ImageVector
    get() {
        if (_Description != null) {
            return _Description!!
        }
        _Description = ImageVector.Builder(
            name = "Outlined.Description",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(320f, 720f)
                lineTo(640f, 720f)
                lineTo(640f, 640f)
                lineTo(320f, 640f)
                lineTo(320f, 720f)
                close()
                moveTo(320f, 560f)
                lineTo(640f, 560f)
                lineTo(640f, 480f)
                lineTo(320f, 480f)
                lineTo(320f, 560f)
                close()
                moveTo(240f, 880f)
                quadTo(207f, 880f, 183.5f, 856.5f)
                quadTo(160f, 833f, 160f, 800f)
                lineTo(160f, 160f)
                quadTo(160f, 127f, 183.5f, 103.5f)
                quadTo(207f, 80f, 240f, 80f)
                lineTo(560f, 80f)
                lineTo(800f, 320f)
                lineTo(800f, 800f)
                quadTo(800f, 833f, 776.5f, 856.5f)
                quadTo(753f, 880f, 720f, 880f)
                lineTo(240f, 880f)
                close()
                moveTo(520f, 360f)
                lineTo(520f, 160f)
                lineTo(240f, 160f)
                quadTo(240f, 160f, 240f, 160f)
                quadTo(240f, 160f, 240f, 160f)
                lineTo(240f, 800f)
                quadTo(240f, 800f, 240f, 800f)
                quadTo(240f, 800f, 240f, 800f)
                lineTo(720f, 800f)
                quadTo(720f, 800f, 720f, 800f)
                quadTo(720f, 800f, 720f, 800f)
                lineTo(720f, 360f)
                lineTo(520f, 360f)
                close()
                moveTo(240f, 160f)
                lineTo(240f, 160f)
                lineTo(240f, 360f)
                lineTo(240f, 360f)
                lineTo(240f, 160f)
                lineTo(240f, 360f)
                lineTo(240f, 360f)
                lineTo(240f, 800f)
                quadTo(240f, 800f, 240f, 800f)
                quadTo(240f, 800f, 240f, 800f)
                lineTo(240f, 800f)
                quadTo(240f, 800f, 240f, 800f)
                quadTo(240f, 800f, 240f, 800f)
                lineTo(240f, 160f)
                quadTo(240f, 160f, 240f, 160f)
                quadTo(240f, 160f, 240f, 160f)
                close()
            }
        }.build()

        return _Description!!
    }

@Suppress("ObjectPropertyName")
private var _Description: ImageVector? = null
