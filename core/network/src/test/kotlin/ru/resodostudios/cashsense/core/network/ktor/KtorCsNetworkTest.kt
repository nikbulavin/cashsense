package ru.resodostudios.cashsense.core.network.ktor

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class KtorCsNetworkTest {

    private val networkJson = Json {
        ignoreUnknownKeys = true
    }

    private fun createDataSource(
        content: String,
        status: HttpStatusCode = HttpStatusCode.OK,
    ): KtorCsNetwork {
        val mockEngine = MockEngine { _ ->
            respond(
                content = content,
                status = status,
                headers = headersOf(
                    HttpHeaders.ContentType,
                    ContentType.Application.Json.toString(),
                ),
            )
        }
        val httpClient = HttpClient(mockEngine) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(networkJson)
            }
            install(Resources)
        }
        return KtorCsNetwork(httpClient)
    }

    @Test
    fun `get currency exchange rate returns correct rate on success`() = runTest {
        val jsonResponse = """
        {
            "status_code": 200,
            "data": {
                "base": "USD",
                "target": "GBP",
                "mid": 0.780945,
                "unit": 1,
                "timestamp": "2024-08-03T05:16:50.272Z"
            }
        }
        """.trimIndent()
        val dataSource = createDataSource(jsonResponse)

        val result = dataSource.getCurrencyExchangeRate("USD", "GBP")

        assertEquals("USD", result.base)
        assertEquals("GBP", result.target)
        assertEquals(0.780945, result.exchangeRate)
    }

    @Test
    fun `throws ClientRequestException with 422 status on invalid currency`() = runTest {
        val errorJson = """
        {
            "status_code": 422,
            "data": {
                "error_code": "INVALID_CURRENCY",
                "message": "ANG has been replaced by XCG.",
                "context": {
                    "replacement_currency": "XCG"
                }
            }
        }
        """.trimIndent()

        val dataSource = createDataSource(
            content = errorJson,
            status = HttpStatusCode.UnprocessableEntity,
        )

        val exception = assertFailsWith<ClientRequestException> {
            dataSource.getCurrencyExchangeRate("ANG", "GBP")
        }

        assertEquals(HttpStatusCode.UnprocessableEntity, exception.response.status)
    }
}
