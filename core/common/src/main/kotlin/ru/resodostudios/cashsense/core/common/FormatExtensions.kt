package ru.resodostudios.cashsense.core.common

import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.Currency
import java.util.Locale

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
