package com.github.adriianh.command

import com.github.adriianh.command.dynamic.convertValues
import com.github.adriianh.service.CurrencyService
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.terminal.Terminal
import kotlinx.coroutines.runBlocking

class ConvertCommand(private val service: CurrencyService) : CliktCommand() {
    private val currencies = runBlocking { service.getCurrencies() }
    private val terminal = Terminal()

    private val currency by option("-c", "--currency", help = "Currency to convert from")
    private val exchanges: List<String>? by option("-e", "--exchange", help = "Currency to convert to")
        .convert { it.split(",").map(String::trim) }
    private val amount by option("-a", "--amount", help = "Amount to convert").double()

    override fun help(context: Context) = "Convert currency from one to another"

    override fun run() {
        if (currencies.isEmpty()) {
            echo("No currencies available to select.")
            return
        }

        if ((currency == null || exchanges == null) && amount == null) {
            convertValues(service, currencies, currentContext)
            return
        }

        if (currency!!.isBlank() || exchanges!!.isEmpty() || amount!!.isNaN()) {
            echo("Currency codes and amount cannot be empty")
            return
        }

        runBlocking {
            exchanges!!.forEach { exchange ->
                val convertedAmount = service.convertCurrency(amount!!, currency!!.lowercase(), exchange.lowercase())

                terminal.print(TextStyles.bold("$amount"))
                terminal.print(" ${currency!!.uppercase()} = ")
                terminal.print(TextStyles.bold("$convertedAmount"))
                terminal.print(TextStyles.bold(" ${exchange.uppercase()}"))
                terminal.println()
            }
        }
    }
}