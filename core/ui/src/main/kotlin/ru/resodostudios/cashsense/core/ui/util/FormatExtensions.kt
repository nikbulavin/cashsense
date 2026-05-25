package ru.resodostudios.cashsense.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLocale
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
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
    plusPrefix: Boolean = false,
    approximatelyPrefix: Boolean = false,
    locale: Locale = Locale.getDefault(),
): String {
    val formattedAmount = getDecimalFormat(currency, locale).format(this)
    return buildString {
        if (approximatelyPrefix && this@formatAmount.signum() > 0) append("≈")
        if (plusPrefix && this@formatAmount.signum() > 0) append("+")
        append(formattedAmount)
    }
}

fun getDecimalFormat(
    currency: Currency,
    locale: Locale = Locale.getDefault(),
): DecimalFormat {
    return (DecimalFormat.getCurrencyInstance(locale) as DecimalFormat).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 2
        this.currency = currency
    }
}

@Composable
fun Instant.formatDate(
    dateFormatType: DateFormatType = DateFormatType.DATE,
    formatStyle: FormatStyle = FormatStyle.MEDIUM,
): String {
    return when (dateFormatType) {
        DateFormatType.DATE_TIME -> DateTimeFormatter.ofLocalizedDateTime(formatStyle)
        DateFormatType.DATE -> DateTimeFormatter.ofLocalizedDate(formatStyle)
        DateFormatType.TIME -> DateTimeFormatter.ofLocalizedTime(formatStyle)
    }
        .withLocale(LocalLocale.current.platformLocale)
        .withZone(LocalTimeZone.current.toJavaZoneId())
        .format(toJavaInstant())
}

@Composable
fun formatDateRange(startDate: LocalDate, endDate: LocalDate): String {
    val currentYear = getCurrentYear()
    val showYear = startDate.year != currentYear || endDate.year != currentYear
    val locale = LocalLocale.current.platformLocale

    val formatter = if (showYear) {
        DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale)
    } else {
        DateTimeFormatter.ofPattern(
            android.text.format.DateFormat.getBestDateTimePattern(locale, "MMMd"),
        ).withLocale(locale)
    }

    return if (startDate == endDate) {
        formatter.format(startDate.toJavaLocalDate())
    } else {
        "${formatter.format(startDate.toJavaLocalDate())} - ${formatter.format(endDate.toJavaLocalDate())}"
    }
}
