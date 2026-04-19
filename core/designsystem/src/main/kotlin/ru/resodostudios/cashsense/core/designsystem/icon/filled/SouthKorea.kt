package ru.resodostudios.cashsense.core.designsystem.icon.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Filled.SouthKorea: ImageVector
    get() {
        if (_SouthKorea != null) {
            return _SouthKorea!!
        }
        _SouthKorea = ImageVector.Builder(
            name = "Filled.SouthKorea",
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(5f, 4f)
                lineTo(27f, 4f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 31f, 8f)
                lineTo(31f, 24f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 27f, 28f)
                lineTo(5f, 28f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1f, 24f)
                lineTo(1f, 8f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5f, 4f)
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
            path(fill = SolidColor(Color.Black)) {
                moveTo(6.572f, 12.438f)
                lineTo(9.086f, 8.666f)
                lineTo(9.714f, 9.085f)
                lineTo(7.2f, 12.857f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(7.515f, 13.067f)
                lineTo(10.029f, 9.295f)
                lineTo(10.657f, 9.714f)
                lineTo(8.143f, 13.486f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(8.456f, 13.695f)
                lineTo(10.97f, 9.924f)
                lineTo(11.598f, 10.342f)
                lineTo(9.084f, 14.114f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(23.648f, 20.871f)
                lineTo(24.8f, 19.143f)
                lineTo(25.428f, 19.562f)
                lineTo(24.276f, 21.29f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(22.285f, 22.915f)
                lineTo(23.438f, 21.186f)
                lineTo(24.066f, 21.605f)
                lineTo(22.913f, 23.334f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(22.704f, 20.243f)
                lineTo(23.856f, 18.515f)
                lineTo(24.484f, 18.933f)
                lineTo(23.332f, 20.662f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(21.342f, 22.286f)
                lineTo(22.494f, 20.558f)
                lineTo(23.123f, 20.977f)
                lineTo(21.971f, 22.705f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(21.762f, 19.615f)
                lineTo(22.914f, 17.887f)
                lineTo(23.542f, 18.305f)
                lineTo(22.39f, 20.034f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(20.401f, 21.657f)
                lineTo(21.553f, 19.928f)
                lineTo(22.181f, 20.347f)
                lineTo(21.029f, 22.075f)
                close()
            }
            path(fill = SolidColor(Color(0xFFBE3B3E))) {
                moveTo(12.229f, 13.486f)
                curveToRelative(1.389f, -2.083f, 4.203f, -2.646f, 6.286f, -1.257f)
                reflectiveCurveToRelative(2.646f, 4.203f, 1.257f, 6.286f)
                lineToRelative(-7.543f, -5.029f)
                close()
            }
            path(fill = SolidColor(Color(0xFF1C449C))) {
                moveTo(12.229f, 13.486f)
                curveToRelative(-1.389f, 2.083f, -0.826f, 4.897f, 1.257f, 6.286f)
                reflectiveCurveToRelative(4.897f, 0.826f, 6.286f, -1.257f)
                curveToRelative(0.694f, -1.041f, 0.413f, -2.449f, -0.629f, -3.143f)
                reflectiveCurveToRelative(-2.449f, -0.413f, -3.143f, 0.629f)
                lineToRelative(-3.771f, -2.514f)
                close()
            }
            path(fill = SolidColor(Color(0xFFBE3B3E))) {
                moveTo(14.114f, 14.743f)
                moveToRelative(-2.266f, 0f)
                arcToRelative(2.266f, 2.266f, 0f, isMoreThanHalf = true, isPositiveArc = true, 4.532f, 0f)
                arcToRelative(2.266f, 2.266f, 0f, isMoreThanHalf = true, isPositiveArc = true, -4.532f, 0f)
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(6.572f, 19.562f)
                lineTo(7.2f, 19.143f)
                lineTo(9.714f, 22.915f)
                lineTo(9.086f, 23.334f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(8.458f, 18.306f)
                lineTo(9.086f, 17.887f)
                lineTo(11.601f, 21.659f)
                lineTo(10.973f, 22.077f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(21.344f, 9.714f)
                lineTo(21.972f, 9.295f)
                lineTo(24.486f, 13.067f)
                lineTo(23.858f, 13.486f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(7.514f, 18.933f)
                lineTo(8.142f, 18.514f)
                lineTo(9.294f, 20.243f)
                lineTo(8.666f, 20.661f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(8.876f, 20.976f)
                lineTo(9.504f, 20.558f)
                lineTo(10.656f, 22.286f)
                lineTo(10.028f, 22.705f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(20.4f, 10.343f)
                lineTo(21.028f, 9.924f)
                lineTo(22.18f, 11.652f)
                lineTo(21.552f, 12.071f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(21.763f, 12.385f)
                lineTo(22.391f, 11.967f)
                lineTo(23.543f, 13.695f)
                lineTo(22.915f, 14.114f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(22.285f, 9.086f)
                lineTo(22.913f, 8.667f)
                lineTo(24.065f, 10.396f)
                lineTo(23.437f, 10.814f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(23.648f, 11.13f)
                lineTo(24.276f, 10.711f)
                lineTo(25.428f, 12.439f)
                lineTo(24.8f, 12.858f)
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

        return _SouthKorea!!
    }

@Suppress("ObjectPropertyName")
private var _SouthKorea: ImageVector? = null
