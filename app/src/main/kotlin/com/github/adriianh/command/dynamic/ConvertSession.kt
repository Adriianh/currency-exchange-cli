package com.github.adriianh.command.dynamic

import com.github.adriianh.command.component.amountInput
import com.github.adriianh.command.component.fromPicker
import com.github.adriianh.command.component.toPicker
import com.github.adriianh.service.CurrenciesResponse
import com.github.adriianh.service.CurrencyService
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.terminal
import com.varabyte.kotter.foundation.session
import com.varabyte.kotter.foundation.text.bold
import com.varabyte.kotter.foundation.text.text
import com.varabyte.kotter.foundation.text.textLine
import kotlinx.coroutines.runBlocking

fun convertValues(service: CurrencyService, response: CurrenciesResponse, context: Context) = session {
    val entries = response.entries.toList()

    val terminalHeight = context.terminal.size.height
    val reservedLines = 10
    val linesPerPage = terminalHeight - reservedLines

    if (response.isEmpty()) {
        section {
            bold { textLine("No currencies available to select.") }
        }.run()
        return@session
    }

    val currency = fromPicker(
        currency = response,
        linesPerPage = linesPerPage,
        totalPages = (entries.size + linesPerPage - 1) / linesPerPage
    )

    val currencies = toPicker(
        currency = response,
        linesPerPage = linesPerPage,
        totalPages = (entries.size + linesPerPage - 1) / linesPerPage
    )

    val amount = amountInput()

    section {
        runBlocking {
            currencies.forEach { exchange ->
                val convertedAmount = service.convertCurrency(amount, currency.first, exchange)

                bold { text("$amount") }
                text(" ${currency.first.uppercase()} = ")
                bold { text("$convertedAmount") }
                text(" ${exchange.uppercase()}")
                textLine()
            }
        }
    }.run()
}