package ru.resodostudios.cashsense.core.ui.util

import androidx.compose.runtime.Composable
import kotlinx.datetime.toJavaZoneId
import ru.resodostudios.cashsense.core.model.data.DateFormatType
import ru.resodostudios.cashsense.core.ui.LocalTimeZone
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Currency
import java.util.Locale
import kotlin.time.Instant
import kotlin.time.toJavaInstant

fun BigDecimal.formatAmount(
    currency: Currency,
    withPlus: Boolean = false,
    withApproximately: Boolean = false,
    locale: Locale = Locale.getDefault(),
): String {
    val formattedAmount = getDecimalFormat(currency, locale).format(this)
    return buildString {
        if (withApproximately && this@formatAmount.signum() > 0) append("≈")
        if (withPlus && this@formatAmount.signum() > 0) append("+")
        append(formattedAmount)
    }
}

fun getDecimalFormat(
    currency: Currency,
    locale: Locale = Locale.getDefault(),
) = DecimalFormat.getCurrencyInstance(locale).apply {
    minimumFractionDigits = 0
    maximumFractionDigits = 2
    this.currency = currency
} as DecimalFormat

@Composable
fun Instant.formatDate(
    dateFormatType: DateFormatType = DateFormatType.DATE,
    formatStyle: FormatStyle = FormatStyle.MEDIUM,
): String =
    when (dateFormatType) {
        DateFormatType.DATE_TIME -> DateTimeFormatter.ofLocalizedDateTime(formatStyle)
        DateFormatType.DATE -> DateTimeFormatter.ofLocalizedDate(formatStyle)
        DateFormatType.TIME -> DateTimeFormatter.ofLocalizedTime(formatStyle)
    }
        .withLocale(Locale.getDefault())
        .withZone(LocalTimeZone.current.toJavaZoneId())
        .format(toJavaInstant())