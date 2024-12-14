package com.github.adriianh.command

import com.github.adriianh.command.dynamic.convertValues
import com.github.adriianh.service.CurrencyService
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.mordant.rendering.BorderType.Companion.SQUARE_DOUBLE_SECTION_SEPARATOR
import com.github.ajalt.mordant.rendering.TextAlign
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextColors.Companion.rgb
import com.github.ajalt.mordant.rendering.TextStyle
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.table.Borders
import com.github.ajalt.mordant.table.table
import kotlinx.coroutines.runBlocking

class ConvertCommand(private val service: CurrencyService) : CliktCommand() {
    private val currencies = runBlocking { service.getCurrencies() }

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

        val table = table {
            borderType = SQUARE_DOUBLE_SECTION_SEPARATOR
            borderStyle = rgb("#4b25b9")
            align = TextAlign.CENTER
            tableBorders = Borders.ALL
            header {
                style = TextColors.brightRed + TextStyles.bold
                row("Currency", "Exchange", "Rate") { cellBorders = Borders.BOTTOM }
            }
            body {
                style = TextColors.green
                rowStyles(TextStyle(), TextStyles.dim.style)

                cellBorders = Borders.ALL
                exchanges!!.forEach { exchange ->
                    val convertedAmount = runBlocking {
                        service.convertCurrency(amount!!, currency!!.lowercase(), exchange.lowercase())
                    }

                    row(currency, exchange, convertedAmount)
                }
            }
        }
        echo(table)
    }
}