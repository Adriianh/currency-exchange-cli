package com.github.adriianh.app

import com.github.adriianh.command.ConvertCommand
import com.github.adriianh.network.HttpClient
import com.github.adriianh.service.CurrencyService
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands

class Exchange : CliktCommand() {
    private val httpClient = HttpClient()
    private val service = CurrencyService(httpClient)

    override fun run() = Unit

    init {
        subcommands(
            ConvertCommand(service)
        )
    }
}

fun main(args: Array<String>) = Exchange().main(args)