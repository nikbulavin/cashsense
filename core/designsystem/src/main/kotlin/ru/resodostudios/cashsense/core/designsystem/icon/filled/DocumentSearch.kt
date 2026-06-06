package ru.resodostudios.cashsense.core.designsystem.icon.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

@Suppress("UnusedReceiverParameter")
val CsIcons.Filled.DocumentSearch: ImageVector
    get() {
        if (_DocumentSearch != null) {
            return _DocumentSearch!!
        }
        _DocumentSearch = ImageVector.Builder(
            name = "Filled.DocumentSearch",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(660f, 760f)
                quadTo(702f, 760f, 731f, 731f)
                quadTo(760f, 702f, 760f, 660f)
                quadTo(760f, 618f, 731f, 589f)
                quadTo(702f, 560f, 660f, 560f)
                quadTo(618f, 560f, 589f, 589f)
                quadTo(560f, 618f, 560f, 660f)
                quadTo(560f, 702f, 589f, 731f)
                quadTo(618f, 760f, 660f, 760f)
                close()
                moveTo(864f, 920f)
                lineTo(756f, 813f)
                quadTo(735f, 826f, 710.5f, 833f)
                quadTo(686f, 840f, 660f, 840f)
                quadTo(585f, 840f, 532.5f, 787.5f)
                quadTo(480f, 735f, 480f, 660f)
                quadTo(480f, 585f, 532.5f, 532.5f)
                quadTo(585f, 480f, 660f, 480f)
                quadTo(735f, 480f, 787.5f, 532.5f)
                quadTo(840f, 585f, 840f, 660f)
                quadTo(840f, 687f, 832.5f, 711.5f)
                quadTo(825f, 736f, 812f, 757f)
                lineTo(920f, 864f)
                lineTo(864f, 920f)
                close()
                moveTo(200f, 880f)
                quadTo(167f, 880f, 143.5f, 856.5f)
                quadTo(120f, 833f, 120f, 800f)
                lineTo(120f, 160f)
                quadTo(120f, 127f, 143.5f, 103.5f)
                quadTo(167f, 80f, 200f, 80f)
                lineTo(520f, 80f)
                lineTo(760f, 320f)
                lineTo(760f, 420f)
                quadTo(736f, 410f, 711f, 405f)
                quadTo(686f, 400f, 660f, 400f)
                quadTo(537f, 400f, 467.5f, 481.5f)
                quadTo(398f, 563f, 398f, 663f)
                quadTo(398f, 725f, 427.5f, 783f)
                quadTo(457f, 841f, 521f, 880f)
                lineTo(200f, 880f)
                close()
                moveTo(480f, 360f)
                lineTo(680f, 360f)
                lineTo(480f, 160f)
                lineTo(680f, 360f)
                lineTo(480f, 160f)
                lineTo(480f, 360f)
                close()
            }
        }.build()

        return _DocumentSearch!!
    }

@Suppress("ObjectPropertyName")
private var _DocumentSearch: ImageVector? = null
