package ru.resodostudios.cashsense.core.designsystem.icon.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Filled.ArrowDropDown: ImageVector
    get() {
        if (_ArrowDropDown != null) {
            return _ArrowDropDown!!
        }
        _ArrowDropDown = ImageVector.Builder(
            name = "Filled.ArrowDropDown",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(480f, 600f)
                lineTo(280f, 400f)
                lineTo(680f, 400f)
                lineTo(480f, 600f)
                close()
            }
        }.build()

        return _ArrowDropDown!!
    }

@Suppress("ObjectPropertyName")
private var _ArrowDropDown: ImageVector? = null
