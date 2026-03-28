package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.Add: ImageVector
    get() {
        if (_Add != null) {
            return _Add!!
        }
        _Add = ImageVector.Builder(
            name = "Outlined.Add",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(440f, 840f)
                lineTo(440f, 520f)
                lineTo(120f, 520f)
                lineTo(120f, 440f)
                lineTo(440f, 440f)
                lineTo(440f, 120f)
                lineTo(520f, 120f)
                lineTo(520f, 440f)
                lineTo(840f, 440f)
                lineTo(840f, 520f)
                lineTo(520f, 520f)
                lineTo(520f, 840f)
                lineTo(440f, 840f)
                close()
            }
        }.build()

        return _Add!!
    }

@Suppress("ObjectPropertyName")
private var _Add: ImageVector? = null
