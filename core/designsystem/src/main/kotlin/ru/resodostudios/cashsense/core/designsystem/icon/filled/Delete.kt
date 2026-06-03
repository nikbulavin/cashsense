package ru.resodostudios.cashsense.core.designsystem.icon.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

@Suppress("UnusedReceiverParameter")
val CsIcons.Filled.Delete: ImageVector
    get() {
        if (_Delete != null) {
            return _Delete!!
        }
        _Delete = ImageVector.Builder(
            name = "Filled.Delete",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(280f, 840f)
                quadTo(247f, 840f, 223.5f, 816.5f)
                quadTo(200f, 793f, 200f, 760f)
                lineTo(200f, 240f)
                lineTo(160f, 240f)
                lineTo(160f, 160f)
                lineTo(360f, 160f)
                lineTo(360f, 120f)
                lineTo(600f, 120f)
                lineTo(600f, 160f)
                lineTo(800f, 160f)
                lineTo(800f, 240f)
                lineTo(760f, 240f)
                lineTo(760f, 760f)
                quadTo(760f, 793f, 736.5f, 816.5f)
                quadTo(713f, 840f, 680f, 840f)
                lineTo(280f, 840f)
                close()
                moveTo(360f, 680f)
                lineTo(440f, 680f)
                lineTo(440f, 320f)
                lineTo(360f, 320f)
                lineTo(360f, 680f)
                close()
                moveTo(520f, 680f)
                lineTo(600f, 680f)
                lineTo(600f, 320f)
                lineTo(520f, 320f)
                lineTo(520f, 680f)
                close()
            }
        }.build()

        return _Delete!!
    }

@Suppress("ObjectPropertyName")
private var _Delete: ImageVector? = null
