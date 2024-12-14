package com.github.adriianh.service

import com.github.adriianh.model.CurrencyResponse
import com.github.adriianh.model.parseRates
import com.github.adriianh.network.HttpClient
import com.github.adriianh.network.URLBuilder
import kotlinx.serialization.json.Json
import java.time.LocalDate

typealias CurrenciesResponse = Map<String, String>

class CurrencyService(private val httpClient: HttpClient) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getCurrencies(): CurrenciesResponse {
        val url = URLBuilder.buildUrl("currencies.json")
        val response = httpClient.get(url.toString())
        return response.parseJson()
    }

    suspend fun getCurrency(currency: String): CurrencyResponse {
        val url = URLBuilder.buildUrl("currencies", "$currency.json")
        val response = httpClient.get(url.toString())

        val currencyResponse = parseRates(response, currency)
        return currencyResponse
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

    suspend fun getCurrencyHistory(from: String, to: String, days: Int, amount: Double?): Map<String, Double> {
        val history = mutableMapOf<String, Double>()

        for (i in 0 until days) {
            val date = LocalDate.now().minusDays(i.toLong()).toString()
            val url = URLBuilder.buildUrlWithVersionPrefix(
                versionPrefix = date,
                "v1", "currencies", "$from.json"
            )

            try {
                if (amount != null) {
                    val response = httpClient.get(url.toString())
                    val currencyResponse = parseRates(response, from)
                    val rate = currencyResponse.rates[to]

                    if (rate != null) {
                        history[date] = amount * rate
                    } else {
                        println("No rate found for $from -> $to on $date")
                    }
                } else {
                    val response = httpClient.get(url.toString())
                    val currencyResponse = parseRates(response, from)
                    val rate = currencyResponse.rates[to]

                    if (rate != null) {
                        history[date] = rate
                    } else {
                        println("No rate found for $from -> $to on $date")
                    }
                }
            } catch (e: Exception) {
                println("Error while fetching history for $from -> $to on $date: ${e.message}")
                continue
            }
        }

        return history.toSortedMap(compareByDescending { it })
    }
    private inline fun <reified T> String.parseJson(): T {
        return json.decodeFromString(this)
    }
}