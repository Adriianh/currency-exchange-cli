package com.github.adriianh.service

import com.github.adriianh.model.parseRates
import com.github.adriianh.network.HttpClient
import com.github.adriianh.network.URLBuilder
import kotlinx.serialization.json.Json

typealias CurrenciesResponse = Map<String, String>

class CurrencyService(private val httpClient: HttpClient) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getCurrencies(): CurrenciesResponse {
        val url = URLBuilder.buildUrl("currencies.json")
        val response = httpClient.get(url.toString())
        return response.parseJson()
    }

    suspend fun convertCurrency(amount: Double, fromCurrency: String, toCurrency: String): Double {
        if (fromCurrency.isBlank() || toCurrency.isBlank()) {
            throw IllegalArgumentException("Currency codes cannot be empty")
        }

        val url = URLBuilder.buildUrl("currencies", "$fromCurrency.json")
        val response = httpClient.get(url.toString())
        val currencyResponse = parseRates(response, fromCurrency)

        val rate = currencyResponse.rates[toCurrency]
            ?: throw IllegalArgumentException("Target currency not found: $toCurrency")

        return amount * rate
    }

    private inline fun <reified T> String.parseJson(): T {
        return json.decodeFromString(this)
    }
}