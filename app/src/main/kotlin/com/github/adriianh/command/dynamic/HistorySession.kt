package com.github.adriianh.command.dynamic

import com.github.adriianh.service.CurrencyService
import com.varabyte.kotter.foundation.input.Completions
import com.varabyte.kotter.foundation.input.input
import com.varabyte.kotter.foundation.input.onInputEntered
import com.varabyte.kotter.foundation.input.runUntilInputEntered
import com.varabyte.kotter.foundation.liveVarOf
import com.varabyte.kotter.foundation.session
import com.varabyte.kotter.foundation.text.text
import com.varabyte.kotter.foundation.text.textLine
import kotlinx.coroutines.runBlocking

fun fetchHistory(service: CurrencyService) = session {
    val currencies = runBlocking { service.getCurrencies() }

    var fromCurrency by liveVarOf("")
    var toCurrency by liveVarOf("")
    var days by liveVarOf(0)
    var amount by liveVarOf(1.0)

    section {
        text("Please enter the currency code you want to fetch history for: ")
        textLine()
        text("> ")
        input(Completions(values = currencies.keys.toTypedArray()))
        textLine()
    }.runUntilInputEntered {
        onInputEntered {
            fromCurrency = input.lowercase()
            if (fromCurrency.isBlank()) {
                section {
                    textLine("Currency code cannot be empty.")
                }.run()
                rejectInput()
            } else {
                signal()
            }
        }
    }

    section {
        text("Please enter the target currency code: ")
        textLine()
        text("> ")
        input(Completions(values = currencies.keys.toTypedArray()))
        textLine()
    }.runUntilInputEntered {
        onInputEntered {
            toCurrency = input.lowercase()
            if (toCurrency.isBlank()) {
                section {
                    textLine("Currency code cannot be empty.")
                }.run()
                rejectInput()
            } else {
                signal()
            }
        }
    }

    section {
        text("Please enter the number of days to fetch history for: ")
        textLine()
        text("> ")
        input(Completions(values = (1..365).map { it.toString() }.toTypedArray()))
        textLine()
    }.runUntilInputEntered {
        onInputEntered {
            days = input.toInt()
            if (days < 1) {
                section {
                    textLine("Days must be greater than 0.")
                }.run()
                rejectInput()
            } else {
                signal()
            }
        }
    }

    section {
        text("Please enter the amount to convert (default is 1): ")
        textLine()
        text("> ")
        input()
        textLine()
    }.runUntilInputEntered {
        onInputEntered {
            amount = input.toDoubleOrNull() ?: 1.0
            signal()
        }
    }

    runBlocking {
        val history = service.getCurrencyHistory(fromCurrency, toCurrency, days, amount)
        if (history.isEmpty()) {
            section {
                textLine("No historical rates found for the given range.")
            }.run()
        } else {
            section {
                textLine("Currency history (from most recent):")
                history.forEach { (date, rate) ->
                    textLine("$fromCurrency = $rate $toCurrency on $date")
                }
            }.run()
        }
    }
}