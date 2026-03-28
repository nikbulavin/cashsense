package ru.resodostudios.cashsense.core.designsystem.icon.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Filled.Policy: ImageVector
    get() {
        if (_Policy != null) {
            return _Policy!!
        }
        _Policy = ImageVector.Builder(
            name = "Filled.Policy",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(480f, 880f)
                quadTo(341f, 845f, 250.5f, 720.5f)
                quadTo(160f, 596f, 160f, 444f)
                lineTo(160f, 200f)
                lineTo(480f, 80f)
                lineTo(800f, 200f)
                lineTo(800f, 444f)
                quadTo(800f, 507f, 783.5f, 566.5f)
                quadTo(767f, 626f, 736f, 680f)
                lineTo(618f, 562f)
                quadTo(629f, 543f, 634.5f, 522.5f)
                quadTo(640f, 502f, 640f, 480f)
                quadTo(640f, 414f, 593f, 367f)
                quadTo(546f, 320f, 480f, 320f)
                quadTo(414f, 320f, 367f, 367f)
                quadTo(320f, 414f, 320f, 480f)
                quadTo(320f, 546f, 367f, 593f)
                quadTo(414f, 640f, 480f, 640f)
                quadTo(501f, 640f, 521.5f, 634.5f)
                quadTo(542f, 629f, 560f, 618f)
                lineTo(689f, 746f)
                quadTo(647f, 795f, 594.5f, 830f)
                quadTo(542f, 865f, 480f, 880f)
                close()
                moveTo(423.5f, 536.5f)
                quadTo(400f, 513f, 400f, 480f)
                quadTo(400f, 447f, 423.5f, 423.5f)
                quadTo(447f, 400f, 480f, 400f)
                quadTo(513f, 400f, 536.5f, 423.5f)
                quadTo(560f, 447f, 560f, 480f)
                quadTo(560f, 513f, 536.5f, 536.5f)
                quadTo(513f, 560f, 480f, 560f)
                quadTo(447f, 560f, 423.5f, 536.5f)
                close()
            }
        }.build()

        return _Policy!!
    }

@Suppress("ObjectPropertyName")
private var _Policy: ImageVector? = null
