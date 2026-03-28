package ru.resodostudios.cashsense.core.designsystem.icon.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Filled.AccountBalance: ImageVector
    get() {
        if (_AccountBalance != null) {
            return _AccountBalance!!
        }
        _AccountBalance = ImageVector.Builder(
            name = "Filled.AccountBalance",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(200f, 680f)
                lineTo(200f, 400f)
                lineTo(280f, 400f)
                lineTo(280f, 680f)
                lineTo(200f, 680f)
                close()
                moveTo(440f, 680f)
                lineTo(440f, 400f)
                lineTo(520f, 400f)
                lineTo(520f, 680f)
                lineTo(440f, 680f)
                close()
                moveTo(80f, 840f)
                lineTo(80f, 760f)
                lineTo(880f, 760f)
                lineTo(880f, 840f)
                lineTo(80f, 840f)
                close()
                moveTo(680f, 680f)
                lineTo(680f, 400f)
                lineTo(760f, 400f)
                lineTo(760f, 680f)
                lineTo(680f, 680f)
                close()
                moveTo(80f, 320f)
                lineTo(80f, 240f)
                lineTo(480f, 40f)
                lineTo(880f, 240f)
                lineTo(880f, 320f)
                lineTo(80f, 320f)
                close()
            }
        }.build()

        return _AccountBalance!!
    }

@Suppress("ObjectPropertyName")
private var _AccountBalance: ImageVector? = null
