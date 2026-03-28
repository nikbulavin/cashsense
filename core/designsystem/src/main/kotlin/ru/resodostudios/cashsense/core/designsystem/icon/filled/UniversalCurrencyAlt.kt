package ru.resodostudios.cashsense.core.designsystem.icon.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Filled.UniversalCurrencyAlt: ImageVector
    get() {
        if (_UniversalCurrencyAlt != null) {
            return _UniversalCurrencyAlt!!
        }
        _UniversalCurrencyAlt = ImageVector.Builder(
            name = "Filled.UniversalCurrencyAlt",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(600f, 640f)
                lineTo(760f, 640f)
                lineTo(760f, 480f)
                lineTo(700f, 480f)
                lineTo(700f, 580f)
                lineTo(600f, 580f)
                lineTo(600f, 640f)
                close()
                moveTo(480f, 600f)
                quadTo(530f, 600f, 565f, 565f)
                quadTo(600f, 530f, 600f, 480f)
                quadTo(600f, 430f, 565f, 395f)
                quadTo(530f, 360f, 480f, 360f)
                quadTo(430f, 360f, 395f, 395f)
                quadTo(360f, 430f, 360f, 480f)
                quadTo(360f, 530f, 395f, 565f)
                quadTo(430f, 600f, 480f, 600f)
                close()
                moveTo(200f, 480f)
                lineTo(260f, 480f)
                lineTo(260f, 380f)
                lineTo(360f, 380f)
                lineTo(360f, 320f)
                lineTo(200f, 320f)
                lineTo(200f, 480f)
                close()
                moveTo(80f, 760f)
                lineTo(80f, 200f)
                lineTo(880f, 200f)
                lineTo(880f, 760f)
                lineTo(80f, 760f)
                close()
            }
        }.build()

        return _UniversalCurrencyAlt!!
    }

@Suppress("ObjectPropertyName")
private var _UniversalCurrencyAlt: ImageVector? = null
