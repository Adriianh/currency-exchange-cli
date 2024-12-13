package com.github.adriianh.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
data class CurrencyResponse(
    val date: String,
    val rates: Map<String, Double>
)

fun parseRates(json: String, baseCurrency: String): CurrencyResponse {
    val jsonObject = Json.parseToJsonElement(json).jsonObject

    val date = jsonObject["date"]?.jsonPrimitive?.content
        ?: throw IllegalArgumentException("Missing 'date' field in response")
    val rates = jsonObject[baseCurrency]?.jsonObject?.mapValues { it.value.jsonPrimitive.double }
        ?: throw IllegalArgumentException("Missing base currency '$baseCurrency' in response")

    return CurrencyResponse(date, rates)
}