package ru.resodostudios.cashsense.core.designsystem.icon.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Filled.Notifications: ImageVector
    get() {
        if (_Notifications != null) {
            return _Notifications!!
        }
        _Notifications = ImageVector.Builder(
            name = "Filled.Notifications",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(160f, 760f)
                lineTo(160f, 680f)
                lineTo(240f, 680f)
                lineTo(240f, 400f)
                quadTo(240f, 317f, 290f, 252.5f)
                quadTo(340f, 188f, 420f, 168f)
                lineTo(420f, 140f)
                quadTo(420f, 115f, 437.5f, 97.5f)
                quadTo(455f, 80f, 480f, 80f)
                quadTo(505f, 80f, 522.5f, 97.5f)
                quadTo(540f, 115f, 540f, 140f)
                lineTo(540f, 168f)
                quadTo(620f, 188f, 670f, 252.5f)
                quadTo(720f, 317f, 720f, 400f)
                lineTo(720f, 680f)
                lineTo(800f, 680f)
                lineTo(800f, 760f)
                lineTo(160f, 760f)
                close()
                moveTo(480f, 880f)
                quadTo(447f, 880f, 423.5f, 856.5f)
                quadTo(400f, 833f, 400f, 800f)
                lineTo(560f, 800f)
                quadTo(560f, 833f, 536.5f, 856.5f)
                quadTo(513f, 880f, 480f, 880f)
                close()
            }
        }.build()

        return _Notifications!!
    }

@Suppress("ObjectPropertyName")
private var _Notifications: ImageVector? = null
