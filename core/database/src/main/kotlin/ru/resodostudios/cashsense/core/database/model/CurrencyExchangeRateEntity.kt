package ru.resodostudios.cashsense.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.model.data.CurrencyExchangeRate
import java.math.BigDecimal
import java.util.Currency

@Entity(
    tableName = "currency_exchange_rates",
)
data class CurrencyExchangeRateEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "base_currency")
    val baseCurrency: Currency,
    @ColumnInfo(name = "target_currency")
    val targetCurrency: Currency,
    @ColumnInfo(name = "exchange_rate")
    val exchangeRate: BigDecimal,
    val timestamp: Instant,
)

fun CurrencyExchangeRateEntity.asExternalModel() =
    CurrencyExchangeRate(
        baseCurrency = baseCurrency,
        targetCurrency = targetCurrency,
        exchangeRate = exchangeRate,
    )
