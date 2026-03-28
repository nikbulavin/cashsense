package ru.resodostudios.cashsense.core.designsystem.icon.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Filled.Gavel: ImageVector
    get() {
        if (_Gavel != null) {
            return _Gavel!!
        }
        _Gavel = ImageVector.Builder(
            name = "Filled.Gavel",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(160f, 840f)
                lineTo(160f, 760f)
                lineTo(640f, 760f)
                lineTo(640f, 840f)
                lineTo(160f, 840f)
                close()
                moveTo(386f, 646f)
                lineTo(160f, 420f)
                lineTo(244f, 334f)
                lineTo(472f, 560f)
                lineTo(386f, 646f)
                close()
                moveTo(640f, 392f)
                lineTo(414f, 164f)
                lineTo(500f, 80f)
                lineTo(726f, 306f)
                lineTo(640f, 392f)
                close()
                moveTo(824f, 800f)
                lineTo(302f, 278f)
                lineTo(358f, 222f)
                lineTo(880f, 744f)
                lineTo(824f, 800f)
                close()
            }
        }.build()

        return _Gavel!!
    }

@Suppress("ObjectPropertyName")
private var _Gavel: ImageVector? = null
