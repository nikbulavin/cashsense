package ru.resodostudios.cashsense.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.LineHeightStyle.Alignment
import androidx.compose.ui.text.style.LineHeightStyle.Trim
import androidx.compose.ui.unit.sp
import ru.resodostudios.cashsense.core.designsystem.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val googleSansFontFamily = FontFamily(
    Font(resId = R.font.google_sans_regular),
    Font(resId = R.font.google_sans_medium, weight = FontWeight.Medium),
)

internal val csTypography: Typography
    @Composable
    get() {
        val locale = LocalLocale.current
        return remember(locale) {
            val notoSerifFontName = GoogleFont(
                when (locale.language) {
                    "ja" -> "Noto Serif JP"
                    "ko" -> "Noto Serif KR"
                    "zh" -> "Noto Serif SC"
                    else -> "Noto Serif"
                },
            )
            val notoSerifFontFamily = FontFamily(
                Font(googleFont = notoSerifFontName, fontProvider = provider),
                Font(resId = R.font.noto_serif_regular),
                Font(
                    googleFont = notoSerifFontName,
                    fontProvider = provider,
                    weight = FontWeight.Medium,
                ),
                Font(resId = R.font.noto_serif_medium, weight = FontWeight.Medium),
            )

            Typography(
                displayLarge = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 57.sp,
                    lineHeight = 64.sp,
                    fontFamily = notoSerifFontFamily,
                ),
                displayMedium = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 45.sp,
                    lineHeight = 52.sp,
                    fontFamily = notoSerifFontFamily,
                ),
                displaySmall = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 36.sp,
                    lineHeight = 44.sp,
                    fontFamily = notoSerifFontFamily,
                ),
                headlineLarge = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 32.sp,
                    lineHeight = 40.sp,
                    fontFamily = notoSerifFontFamily,
                ),
                headlineMedium = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 28.sp,
                    lineHeight = 36.sp,
                    fontFamily = notoSerifFontFamily,
                ),
                headlineSmall = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 24.sp,
                    lineHeight = 32.sp,
                    fontFamily = notoSerifFontFamily,
                ),
                titleLarge = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp,
                    lineHeight = 28.sp,
                    fontFamily = notoSerifFontFamily,
                    lineHeightStyle = LineHeightStyle(
                        alignment = Alignment.Bottom,
                        trim = Trim.LastLineBottom,
                    ),
                ),
                titleMedium = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontFamily = notoSerifFontFamily,
                ),
                titleSmall = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontFamily = notoSerifFontFamily,
                ),
                bodyLarge = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontFamily = googleSansFontFamily,
                    lineHeightStyle = LineHeightStyle(
                        alignment = Alignment.Center,
                        trim = Trim.LastLineBottom,
                    ),
                ),
                bodyMedium = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontFamily = googleSansFontFamily,
                ),
                bodySmall = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontFamily = googleSansFontFamily,
                ),
                labelLarge = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontFamily = googleSansFontFamily,
                    lineHeightStyle = LineHeightStyle(
                        alignment = Alignment.Center,
                        trim = Trim.LastLineBottom,
                    ),
                ),
                labelMedium = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontFamily = googleSansFontFamily,
                    lineHeightStyle = LineHeightStyle(
                        alignment = Alignment.Center,
                        trim = Trim.LastLineBottom,
                    ),
                ),
                labelSmall = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    fontFamily = googleSansFontFamily,
                    lineHeightStyle = LineHeightStyle(
                        alignment = Alignment.Center,
                        trim = Trim.LastLineBottom,
                    ),
                ),
            )
        }
    }