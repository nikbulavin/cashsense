package ru.resodostudios.cashsense.core.designsystem.icon.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Filled.France: ImageVector
    get() {
        if (_France != null) {
            return _France!!
        }
        _France = ImageVector.Builder(
            name = "Filled.France",
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(10f, 4f)
                horizontalLineTo(22f)
                verticalLineTo(28f)
                horizontalLineTo(10f)
                close()
            }
            path(fill = SolidColor(Color(0xFF092050))) {
                moveTo(5f, 4f)
                horizontalLineToRelative(6f)
                verticalLineTo(28f)
                horizontalLineTo(5f)
                curveToRelative(-2.208f, 0f, -4f, -1.792f, -4f, -4f)
                verticalLineTo(8f)
                curveToRelative(0f, -2.208f, 1.792f, -4f, 4f, -4f)
                close()
            }
            path(fill = SolidColor(Color(0xFFBE2A2C))) {
                moveTo(27f, 28f)
                lineToRelative(-6f, -0f)
                lineTo(21f, 4f)
                lineToRelative(6f, -0f)
                curveToRelative(2.208f, -0f, 4f, 1.792f, 4f, 4f)
                lineTo(31f, 24f)
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

        return _France!!
    }

@Suppress("ObjectPropertyName")
private var _France: ImageVector? = null
