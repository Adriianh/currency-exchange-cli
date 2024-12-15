package com.github.adriianh.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

/**
 * Data class representing a currency response.
 *
 * @property date The date of the currency rates.
 * @property rates A map of currency codes to their exchange rates.
 */
@Serializable
data class CurrencyResponse(
    val date: String,
    val rates: Map<String, Double>
)

/**
 * Parses a JSON string to extract currency rates.
 *
 * @param json The JSON string containing the currency rates.
 * @param baseCurrency The base currency code to extract rates for.
 * @return A CurrencyResponse object containing the parsed rates.
 * @throws IllegalArgumentException If the JSON does not contain the required fields.
 */
fun parseRates(json: String, baseCurrency: String): CurrencyResponse {
    val jsonObject = Json.parseToJsonElement(json).jsonObject

    val date = jsonObject["date"]?.jsonPrimitive?.content
        ?: throw IllegalArgumentException("Missing 'date' field in response")
    val rates = jsonObject[baseCurrency]?.jsonObject?.mapValues { it.value.jsonPrimitive.double }
        ?: throw IllegalArgumentException("Missing base currency '$baseCurrency' in response")

    return CurrencyResponse(date, rates)
}