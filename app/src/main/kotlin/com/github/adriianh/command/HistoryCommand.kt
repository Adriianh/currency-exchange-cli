package com.github.adriianh.command

import com.github.adriianh.command.dynamic.fetchHistory
import com.github.adriianh.service.CurrencyService
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.int
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
                if (history.isEmpty()) {
                    echo("No historical rates found for the given range.")
                } else {
                    echo("Currency history (from most recent):")
                    history.forEach { (date, rate) ->
                        echo("${amount ?: 1} ${from!!.uppercase()} = $rate ${to!!.uppercase()} on $date")
                    }
                }
            } catch (e: Exception) {
                echo("Error while fetching history: ${e.message}")
            }
        }

    }
}