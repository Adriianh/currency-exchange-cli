package com.github.adriianh.command

import com.github.adriianh.command.dynamic.fetchHistory
import com.github.adriianh.service.CurrencyService
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.mordant.rendering.BorderType.Companion.SQUARE_DOUBLE_SECTION_SEPARATOR
import com.github.ajalt.mordant.rendering.TextAlign
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextColors.Companion.rgb
import com.github.ajalt.mordant.rendering.TextStyle
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.table.Borders
import com.github.ajalt.mordant.table.table
import kotlinx.coroutines.runBlocking

class HistoryCommand(private val service: CurrencyService) : CliktCommand() {
    private val from by option("-f", "--from", help = "Currency code to convert from")
    private val to by option("-t", "--to", help = "Currency code to convert to")
    private val days by option("-d", "--days", help = "Number of days to get history").int()
    private val amount by option("-a", "--amount", help = "Amount to convert").double()

    override fun help(context: Context) = "Fetch historical currency rates"

    override fun run() {
        if ((from == null || to == null) && days == null) {
            fetchHistory(service)
            return
        }

        if (days!! < 1) {
            echo("Days must be greater than 0")
            return
        }

        runBlocking {
            echo("Fetching history for $from -> $to over the past $days days...")
            try {
                val history = service.getCurrencyHistory(from!!.lowercase(), to!!.lowercase(), days!!, amount)

                val table = table {
                    borderType = SQUARE_DOUBLE_SECTION_SEPARATOR
                    borderStyle = rgb("#4b25b9")
                    align = TextAlign.CENTER
                    tableBorders = Borders.ALL
                    header {
                        style = TextColors.brightRed + TextStyles.bold
                        row("Currency", "Exchange", "Rate", "Date") { cellBorders = Borders.BOTTOM }
                    }
                    body {
                        style = TextColors.green
                        rowStyles(TextStyle(), TextStyles.dim.style)

                        cellBorders = Borders.ALL
                        history.forEach { (date, rate) ->
                            row(from, to, rate, date)
                        }
                    }
                }

                echo(table)
            } catch (e: Exception) {
                echo("Error while fetching history: ${e.message}")
            }
        }

    }
}