package ru.resodostudios.cashsense.core.designsystem.icon.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Filled.Russia: ImageVector
    get() {
        if (_Russia != null) {
            return _Russia!!
        }
        _Russia = ImageVector.Builder(
            name = "Filled.Russia",
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f
        ).apply {
            path(fill = SolidColor(Color(0xFF1435A1))) {
                moveTo(1f, 11f)
                horizontalLineTo(31f)
                verticalLineTo(21f)
                horizontalLineTo(1f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(5f, 4f)
                horizontalLineTo(27f)
                curveToRelative(2.208f, 0f, 4f, 1.792f, 4f, 4f)
                verticalLineToRelative(4f)
                horizontalLineTo(1f)
                verticalLineToRelative(-4f)
                curveToRelative(0f, -2.208f, 1.792f, -4f, 4f, -4f)
                close()
            }
            path(fill = SolidColor(Color(0xFFC53A28))) {
                moveTo(27f, 28f)
                lineTo(5f, 28f)
                curveToRelative(-2.208f, -0f, -4f, -1.792f, -4f, -4f)
                lineToRelative(-0f, -4f)
                lineTo(31f, 20f)
                lineToRelative(-0f, 4f)
                curveToRelative(-0f, 2.208f, -1.792f, 4f, -4f, 4f)
                close()
            }
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 0.15f,
                strokeAlpha = 0.15f
            ) {
                moveTo(27f, 4f)
                lineTo(5f, 4f)
                curveToRelative(-2.209f, 0f, -4f, 1.791f, -4f, 4f)
                lineTo(1f, 24f)
                curveToRelative(0f, 2.209f, 1.791f, 4f, 4f, 4f)
                lineTo(27f, 28f)
                curveToRelative(2.209f, 0f, 4f, -1.791f, 4f, -4f)
                lineTo(31f, 8f)
                curveToRelative(0f, -2.209f, -1.791f, -4f, -4f, -4f)
                close()
                moveTo(30f, 24f)
                curveToRelative(0f, 1.654f, -1.346f, 3f, -3f, 3f)
                lineTo(5f, 27f)
                curveToRelative(-1.654f, 0f, -3f, -1.346f, -3f, -3f)
                lineTo(2f, 8f)
                curveToRelative(0f, -1.654f, 1.346f, -3f, 3f, -3f)
                lineTo(27f, 5f)
                curveToRelative(1.654f, 0f, 3f, 1.346f, 3f, 3f)
                lineTo(30f, 24f)
                close()
            }
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 0.2f,
                strokeAlpha = 0.2f
            ) {
                moveTo(27f, 5f)
                horizontalLineTo(5f)
                curveToRelative(-1.657f, 0f, -3f, 1.343f, -3f, 3f)
                verticalLineToRelative(1f)
                curveToRelative(0f, -1.657f, 1.343f, -3f, 3f, -3f)
                horizontalLineTo(27f)
                curveToRelative(1.657f, 0f, 3f, 1.343f, 3f, 3f)
                verticalLineToRelative(-1f)
                curveToRelative(0f, -1.657f, -1.343f, -3f, -3f, -3f)
                close()
            }
        }.build()

        return _Russia!!
    }

@Suppress("ObjectPropertyName")
private var _Russia: ImageVector? = null
