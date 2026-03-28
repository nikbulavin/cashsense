package ru.resodostudios.cashsense.core.designsystem.icon.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Filled.Feedback: ImageVector
    get() {
        if (_Feedback != null) {
            return _Feedback!!
        }
        _Feedback = ImageVector.Builder(
            name = "Filled.Feedback",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(80f, 880f)
                lineTo(80f, 160f)
                quadTo(80f, 127f, 103.5f, 103.5f)
                quadTo(127f, 80f, 160f, 80f)
                lineTo(800f, 80f)
                quadTo(833f, 80f, 856.5f, 103.5f)
                quadTo(880f, 127f, 880f, 160f)
                lineTo(880f, 640f)
                quadTo(880f, 673f, 856.5f, 696.5f)
                quadTo(833f, 720f, 800f, 720f)
                lineTo(240f, 720f)
                lineTo(80f, 880f)
                close()
                moveTo(480f, 600f)
                quadTo(497f, 600f, 508.5f, 588.5f)
                quadTo(520f, 577f, 520f, 560f)
                quadTo(520f, 543f, 508.5f, 531.5f)
                quadTo(497f, 520f, 480f, 520f)
                quadTo(463f, 520f, 451.5f, 531.5f)
                quadTo(440f, 543f, 440f, 560f)
                quadTo(440f, 577f, 451.5f, 588.5f)
                quadTo(463f, 600f, 480f, 600f)
                close()
                moveTo(440f, 440f)
                lineTo(520f, 440f)
                lineTo(520f, 200f)
                lineTo(440f, 200f)
                lineTo(440f, 440f)
                close()
            }
        }.build()

        return _Feedback!!
    }

@Suppress("ObjectPropertyName")
private var _Feedback: ImageVector? = null
