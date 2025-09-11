package ru.resodostudios.cashsense.core.designsystem.icon.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Filled.DarkMode: ImageVector
    get() {
        if (_DarkMode != null) {
            return _DarkMode!!
        }
        _DarkMode = ImageVector.Builder(
            name = "Filled.DarkMode",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(480f, 840f)
                quadTo(330f, 840f, 225f, 735f)
                quadTo(120f, 630f, 120f, 480f)
                quadTo(120f, 330f, 225f, 225f)
                quadTo(330f, 120f, 480f, 120f)
                quadTo(494f, 120f, 507.5f, 121f)
                quadTo(521f, 122f, 534f, 124f)
                quadTo(493f, 153f, 468.5f, 199.5f)
                quadTo(444f, 246f, 444f, 300f)
                quadTo(444f, 390f, 507f, 453f)
                quadTo(570f, 516f, 660f, 516f)
                quadTo(715f, 516f, 761f, 491.5f)
                quadTo(807f, 467f, 836f, 426f)
                quadTo(838f, 439f, 839f, 452.5f)
                quadTo(840f, 466f, 840f, 480f)
                quadTo(840f, 630f, 735f, 735f)
                quadTo(630f, 840f, 480f, 840f)
                close()
            }
        }.build()

        return _DarkMode!!
    }

@Suppress("ObjectPropertyName")
private var _DarkMode: ImageVector? = null
