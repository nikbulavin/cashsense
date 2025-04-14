package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.Percent: ImageVector
    get() {
        if (_Percent != null) {
            return _Percent!!
        }
        _Percent = ImageVector.Builder(
            name = "Outlined.Percent",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(300f, 440f)
                quadTo(242f, 440f, 201f, 399f)
                quadTo(160f, 358f, 160f, 300f)
                quadTo(160f, 242f, 201f, 201f)
                quadTo(242f, 160f, 300f, 160f)
                quadTo(358f, 160f, 399f, 201f)
                quadTo(440f, 242f, 440f, 300f)
                quadTo(440f, 358f, 399f, 399f)
                quadTo(358f, 440f, 300f, 440f)
                close()
                moveTo(300f, 360f)
                quadTo(325f, 360f, 342.5f, 342.5f)
                quadTo(360f, 325f, 360f, 300f)
                quadTo(360f, 275f, 342.5f, 257.5f)
                quadTo(325f, 240f, 300f, 240f)
                quadTo(275f, 240f, 257.5f, 257.5f)
                quadTo(240f, 275f, 240f, 300f)
                quadTo(240f, 325f, 257.5f, 342.5f)
                quadTo(275f, 360f, 300f, 360f)
                close()
                moveTo(660f, 800f)
                quadTo(602f, 800f, 561f, 759f)
                quadTo(520f, 718f, 520f, 660f)
                quadTo(520f, 602f, 561f, 561f)
                quadTo(602f, 520f, 660f, 520f)
                quadTo(718f, 520f, 759f, 561f)
                quadTo(800f, 602f, 800f, 660f)
                quadTo(800f, 718f, 759f, 759f)
                quadTo(718f, 800f, 660f, 800f)
                close()
                moveTo(660f, 720f)
                quadTo(685f, 720f, 702.5f, 702.5f)
                quadTo(720f, 685f, 720f, 660f)
                quadTo(720f, 635f, 702.5f, 617.5f)
                quadTo(685f, 600f, 660f, 600f)
                quadTo(635f, 600f, 617.5f, 617.5f)
                quadTo(600f, 635f, 600f, 660f)
                quadTo(600f, 685f, 617.5f, 702.5f)
                quadTo(635f, 720f, 660f, 720f)
                close()
                moveTo(216f, 800f)
                lineTo(160f, 744f)
                lineTo(744f, 160f)
                lineTo(800f, 216f)
                lineTo(216f, 800f)
                close()
            }
        }.build()

        return _Percent!!
    }

@Suppress("ObjectPropertyName")
private var _Percent: ImageVector? = null
