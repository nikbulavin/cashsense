package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.Scooter: ImageVector
    get() {
        if (_Scooter != null) {
            return _Scooter!!
        }
        _Scooter = ImageVector.Builder(
            name = "Outlined.Scooter",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
            autoMirror = true,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(760f, 720f)
                quadTo(777f, 720f, 788.5f, 708.5f)
                quadTo(800f, 697f, 800f, 680f)
                quadTo(800f, 663f, 788.5f, 651.5f)
                quadTo(777f, 640f, 760f, 640f)
                quadTo(743f, 640f, 731.5f, 651.5f)
                quadTo(720f, 663f, 720f, 680f)
                quadTo(720f, 697f, 731.5f, 708.5f)
                quadTo(743f, 720f, 760f, 720f)
                close()
                moveTo(760f, 800f)
                quadTo(710f, 800f, 675f, 765f)
                quadTo(640f, 730f, 640f, 680f)
                quadTo(640f, 630f, 675f, 595f)
                quadTo(710f, 560f, 760f, 560f)
                quadTo(810f, 560f, 845f, 595f)
                quadTo(880f, 630f, 880f, 680f)
                quadTo(880f, 730f, 845f, 765f)
                quadTo(810f, 800f, 760f, 800f)
                close()
                moveTo(200f, 720f)
                quadTo(217f, 720f, 228.5f, 708.5f)
                quadTo(240f, 697f, 240f, 680f)
                quadTo(240f, 663f, 228.5f, 651.5f)
                quadTo(217f, 640f, 200f, 640f)
                quadTo(183f, 640f, 171.5f, 651.5f)
                quadTo(160f, 663f, 160f, 680f)
                quadTo(160f, 697f, 171.5f, 708.5f)
                quadTo(183f, 720f, 200f, 720f)
                close()
                moveTo(200f, 800f)
                quadTo(150f, 800f, 115f, 765f)
                quadTo(80f, 730f, 80f, 680f)
                quadTo(80f, 630f, 115f, 595f)
                quadTo(150f, 560f, 200f, 560f)
                quadTo(238f, 560f, 269f, 582f)
                quadTo(300f, 604f, 313f, 640f)
                lineTo(524f, 640f)
                quadTo(535f, 571f, 580.5f, 520.5f)
                quadTo(626f, 470f, 692f, 450f)
                lineTo(636f, 200f)
                quadTo(636f, 200f, 636f, 200f)
                quadTo(636f, 200f, 636f, 200f)
                lineTo(480f, 200f)
                lineTo(480f, 120f)
                lineTo(636f, 120f)
                quadTo(664f, 120f, 686f, 137f)
                quadTo(708f, 154f, 714f, 182f)
                lineTo(790f, 520f)
                lineTo(760f, 520f)
                quadTo(694f, 520f, 647f, 567f)
                quadTo(600f, 614f, 600f, 680f)
                lineTo(600f, 720f)
                lineTo(313f, 720f)
                quadTo(300f, 756f, 269f, 778f)
                quadTo(238f, 800f, 200f, 800f)
                close()
            }
        }.build()

        return _Scooter!!
    }

@Suppress("ObjectPropertyName")
private var _Scooter: ImageVector? = null
