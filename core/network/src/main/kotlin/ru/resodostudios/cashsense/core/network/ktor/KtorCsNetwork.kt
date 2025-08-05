package ru.resodostudios.cashsense.core.network.ktor

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.core.network.CsNetworkDataSource
import ru.resodostudios.cashsense.core.network.model.NetworkCurrencyExchangeRate
import ru.resodostudios.cashsense.core.network.resource.CurrencyRateResource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class KtorCsNetwork @Inject constructor(
    private val httpClient: HttpClient,
) : CsNetworkDataSource {

    override suspend fun getCurrencyExchangeRate(
        baseCurrencyCode: String,
        targetCurrencyCode: String,
    ): NetworkCurrencyExchangeRate {
        return httpClient
            .get(CurrencyRateResource(baseCurrencyCode, targetCurrencyCode))
            .body<NetworkResponse<NetworkCurrencyExchangeRate>>()
            .data
    }
}

@Serializable
private data class NetworkResponse<T>(
    val data: T,
)