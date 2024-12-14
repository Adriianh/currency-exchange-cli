package com.github.adriianh.command.component

import com.varabyte.kotter.foundation.input.input
import com.varabyte.kotter.foundation.input.onInputEntered
import com.varabyte.kotter.foundation.input.runUntilInputEntered
import com.varabyte.kotter.foundation.text.green
import com.varabyte.kotter.foundation.text.red
import com.varabyte.kotter.foundation.text.text
import com.varabyte.kotter.foundation.text.textLine
import com.varabyte.kotter.runtime.Session

fun Session.amountInput(): Double {
    var amount = 0.0

    section {
        textLine("Type in an amount. Invalid values will be highlighted in red.")
        text("> ")
        input(
            customFormat = {
                when {
                    ch.isDigit() -> green()
                    else -> red()
                }
            }
        )
        textLine()
    }.runUntilInputEntered {
        onInputEntered {
            if (input.any { !it.isDigit() }) rejectInput()
            else amount = input.toDouble()
        }
    }

    return amount
}