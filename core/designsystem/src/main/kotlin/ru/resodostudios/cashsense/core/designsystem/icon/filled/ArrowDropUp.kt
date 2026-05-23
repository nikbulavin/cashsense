package ru.resodostudios.cashsense.core.designsystem.icon.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Filled.ArrowDropUp: ImageVector
    get() {
        if (_ArrowDropUp != null) {
            return _ArrowDropUp!!
        }
        _ArrowDropUp = ImageVector.Builder(
            name = "Filled.ArrowDropUp",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(280f, 560f)
                lineTo(480f, 360f)
                lineTo(680f, 560f)
                lineTo(280f, 560f)
                close()
            }
        }.build()

        return _ArrowDropUp!!
    }

@Suppress("ObjectPropertyName")
private var _ArrowDropUp: ImageVector? = null
