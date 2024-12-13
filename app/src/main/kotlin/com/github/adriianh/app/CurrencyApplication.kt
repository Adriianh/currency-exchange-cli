package com.github.adriianh.app

import com.github.adriianh.service.CurrencyService

class CurrencyApplication(private val currencyService: CurrencyService) {
    suspend fun run() {
        try {
            val currencies = currencyService.getCurrencies()
            println("Currencies: $currencies")

            val convertedAmount = currencyService.convertCurrency(100.0, "usd", "eur")
            println("Converted amount: $convertedAmount")
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
}