package com.github.adriianh.service

import com.github.adriianh.model.CurrencyResponse
import com.github.adriianh.model.parseRates
import com.github.adriianh.network.HttpClient
import com.github.adriianh.network.URLBuilder
import kotlinx.serialization.json.Json
import java.time.LocalDate

typealias CurrenciesResponse = Map<String, String>

/**
 * Service class for handling currency-related operations.
 *
 * @property httpClient The HTTP client used for making requests.
 */
class CurrencyService(private val httpClient: HttpClient) {
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Fetches the list of available currencies.
     *
     * @return A map of currency codes to currency names.
     */
    suspend fun getCurrencies(): CurrenciesResponse {
        val url = URLBuilder.buildUrl("currencies.json")
        val response = httpClient.get(url.toString())
        return response.parseJson()
    }

    /**
     * Fetches the exchange rates for a specific currency.
     *
     * @param currency The currency code to fetch rates for.
     * @return A CurrencyResponse object containing the exchange rates.
     */
    suspend fun getCurrency(currency: String): CurrencyResponse {
        val url = URLBuilder.buildUrl("currencies", "$currency.json")
        val response = httpClient.get(url.toString())

        val currencyResponse = parseRates(response, currency)
        return currencyResponse
    }

    /**
     * Converts an amount from one currency to another.
     *
     * @param amount The amount to convert.
     * @param fromCurrency The currency code to convert from.
     * @param toCurrency The currency code to convert to.
     * @return The converted amount.
     * @throws IllegalArgumentException If the currency codes are empty or the target currency is not found.
     */
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

    /**
     * Fetches the historical exchange rates for a currency pair over a specified number of days.
     *
     * @param from The base currency code.
     * @param to The target currency code.
     * @param days The number of days to fetch history for.
     * @param amount The amount to convert (optional).
     * @return A map of dates to exchange rates or converted amounts.
     */
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

    /**
     * Parses a JSON string into an object of type T.
     *
     * @param T The type of the object to parse.
     * @return The parsed object.
     */
    private inline fun <reified T> String.parseJson(): T {
        return json.decodeFromString(this)
    }
}