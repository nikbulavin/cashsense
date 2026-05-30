package ru.resodostudios.cashsense.core.domain

import kotlinx.coroutines.flow.first
import ru.resodostudios.cashsense.core.common.CsvParser
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
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
    private val transactionsRepository: TransactionsRepository,
    private val walletsRepository: WalletsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val csvParser: CsvParser,
) {

    suspend operator fun invoke(
        walletId: String,
        lines: List<String>,
        config: CsvConfig,
    ): Result<Int> = runCatching {
        val extendedWallet = walletsRepository.getExtendedWallet(walletId).first()
        val existingTransactions = extendedWallet.transactions
        val categories = categoriesRepository.getCategories().first()

        val formatter = DateTimeFormatter.ofPattern(config.dateFormat)

        val transactionsToImport = lines
            .asSequence()
            .drop(if (config.ignoreFirstLine) 1 else 0)
            .filter { it.isNotBlank() }
            .map { line ->
                val columns = csvParser.parse(line, config.columnSeparator)
                val rawAmount = columns[config.amountColumnIndex]
                val rawDate = columns[config.dateColumnIndex]
                val description = columns.getOrNull(config.descriptionColumnIndex)
                val rawCategory = columns.getOrNull(config.categoryColumnIndex)

                val amount = BigDecimal(rawAmount.replace(",", "."))
                val javaInstant = LocalDateTime.parse(rawDate, formatter)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
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
            }
            .filter { newTransaction ->
                existingTransactions.none { existing ->
                    existing.timestamp == newTransaction.timestamp &&
                            existing.amount.compareTo(newTransaction.amount) == 0 &&
                            existing.description == newTransaction.description
                }
            }
            .toList()

        transactionsToImport.forEach {
            transactionsRepository.upsertTransaction(it)
        }

        transactionsToImport.size
    }
}
