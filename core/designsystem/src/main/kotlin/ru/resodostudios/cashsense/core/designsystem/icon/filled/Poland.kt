package ru.resodostudios.cashsense.core.designsystem.icon.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Filled.Poland: ImageVector
    get() {
        if (_Poland != null) {
            return _Poland!!
        }
        _Poland = ImageVector.Builder(
            name = "Filled.Poland",
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f
        ).apply {
            path(fill = SolidColor(Color(0xFFCB2E40))) {
                moveTo(1f, 24f)
                curveToRelative(0f, 2.209f, 1.791f, 4f, 4f, 4f)
                horizontalLineTo(27f)
                curveToRelative(2.209f, 0f, 4f, -1.791f, 4f, -4f)
                verticalLineTo(15f)
                horizontalLineTo(1f)
                verticalLineToRelative(9f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(27f, 4f)
                horizontalLineTo(5f)
                curveToRelative(-2.209f, 0f, -4f, 1.791f, -4f, 4f)
                verticalLineToRelative(8f)
                horizontalLineTo(31f)
                verticalLineTo(8f)
                curveToRelative(0f, -2.209f, -1.791f, -4f, -4f, -4f)
                close()
            }
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 0.15f,
                strokeAlpha = 0.15f
            ) {
                moveTo(5f, 28f)
                horizontalLineTo(27f)
                curveToRelative(2.209f, 0f, 4f, -1.791f, 4f, -4f)
                verticalLineTo(8f)
                curveToRelative(0f, -2.209f, -1.791f, -4f, -4f, -4f)
                horizontalLineTo(5f)
                curveToRelative(-2.209f, 0f, -4f, 1.791f, -4f, 4f)
                verticalLineTo(24f)
                curveToRelative(0f, 2.209f, 1.791f, 4f, 4f, 4f)
                close()
                moveTo(2f, 8f)
                curveToRelative(0f, -1.654f, 1.346f, -3f, 3f, -3f)
                horizontalLineTo(27f)
                curveToRelative(1.654f, 0f, 3f, 1.346f, 3f, 3f)
                verticalLineTo(24f)
                curveToRelative(0f, 1.654f, -1.346f, 3f, -3f, 3f)
                horizontalLineTo(5f)
                curveToRelative(-1.654f, 0f, -3f, -1.346f, -3f, -3f)
                verticalLineTo(8f)
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

        return _Poland!!
    }

@Suppress("ObjectPropertyName")
private var _Poland: ImageVector? = null
