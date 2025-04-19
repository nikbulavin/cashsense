package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.SettingsBackupRestore: ImageVector
    get() {
        if (_SettingsBackupRestore != null) {
            return _SettingsBackupRestore!!
        }
        _SettingsBackupRestore = ImageVector.Builder(
            name = "Outlined.SettingsBackupRestore",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
            autoMirror = true,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(480f, 560f)
                quadTo(447f, 560f, 423.5f, 536.5f)
                quadTo(400f, 513f, 400f, 480f)
                quadTo(400f, 447f, 423.5f, 423.5f)
                quadTo(447f, 400f, 480f, 400f)
                quadTo(513f, 400f, 536.5f, 423.5f)
                quadTo(560f, 447f, 560f, 480f)
                quadTo(560f, 513f, 536.5f, 536.5f)
                quadTo(513f, 560f, 480f, 560f)
                close()
                moveTo(480f, 840f)
                quadTo(341f, 840f, 239f, 748.5f)
                quadTo(137f, 657f, 122f, 520f)
                lineTo(204f, 520f)
                quadTo(218f, 624f, 296.5f, 692f)
                quadTo(375f, 760f, 480f, 760f)
                quadTo(597f, 760f, 678.5f, 678.5f)
                quadTo(760f, 597f, 760f, 480f)
                quadTo(760f, 363f, 678.5f, 281.5f)
                quadTo(597f, 200f, 480f, 200f)
                quadTo(411f, 200f, 351f, 232f)
                quadTo(291f, 264f, 250f, 320f)
                lineTo(360f, 320f)
                lineTo(360f, 400f)
                lineTo(120f, 400f)
                lineTo(120f, 160f)
                lineTo(200f, 160f)
                lineTo(200f, 254f)
                quadTo(251f, 190f, 324.5f, 155f)
                quadTo(398f, 120f, 480f, 120f)
                quadTo(555f, 120f, 620.5f, 148.5f)
                quadTo(686f, 177f, 734.5f, 225.5f)
                quadTo(783f, 274f, 811.5f, 339.5f)
                quadTo(840f, 405f, 840f, 480f)
                quadTo(840f, 555f, 811.5f, 620.5f)
                quadTo(783f, 686f, 734.5f, 734.5f)
                quadTo(686f, 783f, 620.5f, 811.5f)
                quadTo(555f, 840f, 480f, 840f)
                close()
            }
        }.build()

        return _SettingsBackupRestore!!
    }

@Suppress("ObjectPropertyName")
private var _SettingsBackupRestore: ImageVector? = null
