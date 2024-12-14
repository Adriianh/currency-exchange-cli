package com.github.adriianh.command

import com.github.adriianh.command.dynamic.fetchCurrency
import com.github.adriianh.service.CurrencyService
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.options.option
import com.varabyte.kotter.foundation.input.Keys
import com.varabyte.kotter.foundation.input.onKeyPressed
import com.varabyte.kotter.foundation.liveVarOf
import com.varabyte.kotter.foundation.runUntilSignal
import com.varabyte.kotter.foundation.session
import com.varabyte.kotter.foundation.text.black
import com.varabyte.kotter.foundation.text.bold
import com.varabyte.kotter.foundation.text.textLine
import kotlinx.coroutines.runBlocking
import kotlin.math.max
import kotlin.math.min

class FetchCommand(private val service: CurrencyService) : CliktCommand() {
    private val currency by option("-c", "--currency", help = "Currency to fetch")

    override fun help(context: Context): String = "Fetches the latest currency exchange rates from the internet."

    override fun run() {
        if (currency == null) {
            fetchCurrency(service, currentContext)
            return
        }

        if (currency!!.isBlank()) {
            echo("Currency code cannot be empty.")
            return
        }

        echo("Fetching currency: $currency")
        val response = runBlocking { service.getCurrency(currency!!.lowercase()) }

        session {
            val terminalHeight = terminal.size.height
            val reservedLines = 5
            val itemsPerPage = terminalHeight - reservedLines
            val rateEntries = response.rates.toList()
            val totalPages = (rateEntries.size + itemsPerPage - 1) / itemsPerPage

            var currentPage by liveVarOf(0)

            section {
                bold { textLine("Currency Rates") }
                textLine()

                val startIndex = currentPage * itemsPerPage
                val endIndex = min((currentPage + 1) * itemsPerPage, rateEntries.size)

                for (index in startIndex until endIndex) {
                    val (key, value) = rateEntries[index]
                    textLine("$key = $value")
                }

                textLine()
                black(isBright = true) {
                    textLine("Page ${currentPage + 1} of $totalPages")
                    textLine("Use the LEFT/RIGHT keys to change pages, ESC to exit.")
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
}