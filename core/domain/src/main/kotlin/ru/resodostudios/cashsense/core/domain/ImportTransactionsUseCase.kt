package ru.resodostudios.cashsense.core.domain

import com.jsoizo.kotlincsv.CsvDialect
import com.jsoizo.kotlincsv.csvReader
import kotlinx.coroutines.flow.first
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.CsvConfig
import ru.resodostudios.cashsense.core.model.data.Transaction
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.time.Instant
import kotlin.uuid.Uuid

class ImportTransactionsUseCase @Inject constructor(
    private val walletsRepository: WalletsRepository,
    private val categoriesRepository: CategoriesRepository,
) {

    suspend operator fun invoke(
        walletId: String,
        lines: List<String>,
        config: CsvConfig,
    ): Result<List<Transaction>> = runCatching {
        val extendedWallet = walletsRepository.getExtendedWallet(walletId).first()
        val existingTransactions = extendedWallet.transactions
        val categories = categoriesRepository.getCategories().first()
        val formatter = DateTimeFormatter.ofPattern(config.dateFormat)

        val reader = csvReader {
            dialect = CsvDialect(delimiter = config.columnSeparator.firstOrNull() ?: ';')
            skipEmptyLine = true
        }
        val allRows = reader.readAll(lines.joinToString("\n"))
        val rowsToImport = if (config.ignoreFirstLine) allRows.drop(1) else allRows

        rowsToImport.asSequence().mapNotNull { columns ->
            val rawAmount = columns.getOrNull(config.amountColumnIndex)
            val rawDate = columns.getOrNull(config.dateColumnIndex)

            if (!rawAmount.isNullOrBlank() && !rawDate.isNullOrBlank()) {
                val description = columns.getOrNull(config.descriptionColumnIndex)
                val rawCategory = columns.getOrNull(config.categoryColumnIndex)

                val amount = runCatching { BigDecimal(rawAmount.replace(",", ".")) }.getOrNull() ?: return@mapNotNull null
                val javaInstant = runCatching {
                    LocalDateTime.parse(rawDate, formatter)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                }.getOrNull() ?: return@mapNotNull null
                val timestamp = Instant.fromEpochMilliseconds(javaInstant.toEpochMilli())

                val category = categories.find { it.title.equals(rawCategory, ignoreCase = true) }

                Transaction(
                    id = Uuid.random().toHexString(),
                    walletOwnerId = walletId,
                    description = description,
                    amount = amount,
                    timestamp = timestamp,
                    completed = true,
                    ignored = false,
                    transferId = null,
                    currency = extendedWallet.wallet.currency,
                    category = category,
                )
            } else {
                null
            }
        }.filter { newTransaction ->
            existingTransactions.none { existing ->
                (existing.timestamp == newTransaction.timestamp) &&
                        (existing.amount.compareTo(newTransaction.amount) == 0) &&
                        (existing.description == newTransaction.description)
            }
        }.toList()
    }
}
