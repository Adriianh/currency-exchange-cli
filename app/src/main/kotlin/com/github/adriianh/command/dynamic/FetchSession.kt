package com.github.adriianh.command.dynamic

import com.github.adriianh.service.CurrencyService
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.terminal
import com.varabyte.kotter.foundation.input.*
import com.varabyte.kotter.foundation.liveVarOf
import com.varabyte.kotter.foundation.runUntilSignal
import com.varabyte.kotter.foundation.session
import com.varabyte.kotter.foundation.text.black
import com.varabyte.kotter.foundation.text.bold
import com.varabyte.kotter.foundation.text.text
import com.varabyte.kotter.foundation.text.textLine
import kotlinx.coroutines.runBlocking
import kotlin.math.max
import kotlin.math.min

fun fetchCurrency(service: CurrencyService, context: Context) = session {
    val currencies = runBlocking { service.getCurrencies() }

    var currency by liveVarOf("")

    section {
        text("Please enter the currency code you want to fetch: "); textLine()
        text("> ")
        input(Completions(values = currencies.keys.toTypedArray()))

        textLine()
    }.runUntilInputEntered {
        onInputEntered {
            currency = input.lowercase()
            if (currency.isBlank()) {
                section {
                    textLine("Currency code cannot be empty.")
                }.run()
                rejectInput()
            } else {
                signal()
            }
        }
    }

    runBlocking {
        val response = service.getCurrency(currency)
        val entries = response.rates.toList()
        val terminalHeight = context.terminal.size.height
        val reservedLines = 5
        val linesPerPage = terminalHeight - reservedLines
        val totalPages = (entries.size + linesPerPage - 1) / linesPerPage

        if (entries.isEmpty()) {
            section {
                bold { textLine("No items to display.") }
            }.run()
            return@runBlocking
        }

        var currentPage by liveVarOf(0)

        section {
            bold { textLine("Fetched currency response:") }
            textLine()

            val startIndex = currentPage * linesPerPage
            val endIndex = min((currentPage + 1) * linesPerPage, entries.size)

            for (index in startIndex until endIndex) {
                val (code, rate) = entries[index]
                textLine("$code = $rate")
            }

            textLine()
            black(isBright = true) {
                textLine("Page ${currentPage + 1} of $totalPages")
                textLine("Use the LEFT/RIGHT keys to navigate between pages.")
                textLine("Press ESC to exit.")
            }
        }.runUntilSignal {
            onKeyPressed {
                when (key) {
                    Keys.LEFT -> {
                        currentPage = max(currentPage - 1, 0)
                        rerender()
                    }

                    Keys.RIGHT -> {
                        currentPage = min(currentPage + 1, totalPages - 1)
                        rerender()
                    }

                    Keys.ESC -> signal()
                    else -> {}
                }
            }
        }
    }
}