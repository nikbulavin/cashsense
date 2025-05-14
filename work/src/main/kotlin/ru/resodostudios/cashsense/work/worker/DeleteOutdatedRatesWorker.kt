package ru.resodostudios.cashsense.work.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.tracing.traceAsync
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import ru.resodostudios.cashsense.core.data.repository.CurrencyConversionRepository
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.database.dao.CurrencyConversionDao
import ru.resodostudios.cashsense.core.network.CsDispatchers
import ru.resodostudios.cashsense.core.network.Dispatcher
import ru.resodostudios.cashsense.work.initializer.SyncConstraints
import java.util.Currency
import java.util.concurrent.TimeUnit

@HiltWorker
internal class DeleteOutdatedRatesWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val currencyConversionRepository: CurrencyConversionRepository,
    private val walletsRepository: WalletsRepository,
    private val userDataRepository: UserDataRepository,
    @Dispatcher(CsDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val dao: CurrencyConversionDao,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        traceAsync("DeleteOutdatedRates", 0) {
            currencyConversionRepository.deleteOutdatedCurrencyExchangeRates()
            val baseCurrencies = async {
                walletsRepository.getDistinctCurrencies().first().toSet()
            }
            val userCurrency = async {
                Currency.getInstance(userDataRepository.userData.first().currency)
            }
            val exchangeRates = runCatching {
                currencyConversionRepository.getCurrencyExchangeRates(
                    baseCurrencies = baseCurrencies.await(),
                    targetCurrency = userCurrency.await(),
                )
            }.getOrNull()
            if (exchangeRates != null) {
                currencyConversionRepository.deleteOutdatedCurrencyExchangeRates()
                dao.upsertCurrencyExchangeRates(exchangeRates)
                Result.success()
            } else {
                Result.retry()
            }
        }
    }

    companion object {

        fun periodicDeleteOutdatedRatesWork() =
            PeriodicWorkRequestBuilder<DelegatingWorker>(
                repeatInterval = 1,
                repeatIntervalTimeUnit = TimeUnit.DAYS,
            )
                .setConstraints(SyncConstraints)
                .setInputData(DeleteOutdatedRatesWorker::class.delegatedData())
                .build()
    }
}