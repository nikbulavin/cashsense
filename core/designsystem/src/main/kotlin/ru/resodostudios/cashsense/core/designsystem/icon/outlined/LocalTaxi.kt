package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.LocalTaxi: ImageVector
    get() {
        if (_LocalTaxi != null) {
            return _LocalTaxi!!
        }
        _LocalTaxi = ImageVector.Builder(
            name = "LocalTaxi",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(240f, 760f)
                lineTo(240f, 800f)
                quadTo(240f, 817f, 228.5f, 828.5f)
                quadTo(217f, 840f, 200f, 840f)
                lineTo(160f, 840f)
                quadTo(143f, 840f, 131.5f, 828.5f)
                quadTo(120f, 817f, 120f, 800f)
                lineTo(120f, 480f)
                lineTo(204f, 240f)
                quadTo(210f, 222f, 225.5f, 211f)
                quadTo(241f, 200f, 260f, 200f)
                lineTo(360f, 200f)
                lineTo(360f, 120f)
                lineTo(600f, 120f)
                lineTo(600f, 200f)
                lineTo(700f, 200f)
                quadTo(719f, 200f, 734.5f, 211f)
                quadTo(750f, 222f, 756f, 240f)
                lineTo(840f, 480f)
                lineTo(840f, 800f)
                quadTo(840f, 817f, 828.5f, 828.5f)
                quadTo(817f, 840f, 800f, 840f)
                lineTo(760f, 840f)
                quadTo(743f, 840f, 731.5f, 828.5f)
                quadTo(720f, 817f, 720f, 800f)
                lineTo(720f, 760f)
                lineTo(240f, 760f)
                close()
                moveTo(232f, 400f)
                lineTo(728f, 400f)
                lineTo(686f, 280f)
                lineTo(274f, 280f)
                lineTo(232f, 400f)
                close()
                moveTo(200f, 480f)
                lineTo(200f, 480f)
                lineTo(200f, 680f)
                lineTo(200f, 680f)
                lineTo(200f, 480f)
                close()
                moveTo(300f, 640f)
                quadTo(325f, 640f, 342.5f, 622.5f)
                quadTo(360f, 605f, 360f, 580f)
                quadTo(360f, 555f, 342.5f, 537.5f)
                quadTo(325f, 520f, 300f, 520f)
                quadTo(275f, 520f, 257.5f, 537.5f)
                quadTo(240f, 555f, 240f, 580f)
                quadTo(240f, 605f, 257.5f, 622.5f)
                quadTo(275f, 640f, 300f, 640f)
                close()
                moveTo(660f, 640f)
                quadTo(685f, 640f, 702.5f, 622.5f)
                quadTo(720f, 605f, 720f, 580f)
                quadTo(720f, 555f, 702.5f, 537.5f)
                quadTo(685f, 520f, 660f, 520f)
                quadTo(635f, 520f, 617.5f, 537.5f)
                quadTo(600f, 555f, 600f, 580f)
                quadTo(600f, 605f, 617.5f, 622.5f)
                quadTo(635f, 640f, 660f, 640f)
                close()
                moveTo(200f, 680f)
                lineTo(760f, 680f)
                lineTo(760f, 480f)
                lineTo(200f, 480f)
                lineTo(200f, 680f)
                close()
            }
        }.build()

        return _LocalTaxi!!
    }

@Suppress("ObjectPropertyName")
private var _LocalTaxi: ImageVector? = null