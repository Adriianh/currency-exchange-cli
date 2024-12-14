package com.github.adriianh.command.component

import com.github.adriianh.service.CurrenciesResponse
import com.varabyte.kotter.foundation.collections.liveSetOf
import com.varabyte.kotter.foundation.input.Keys
import com.varabyte.kotter.foundation.input.onKeyPressed
import com.varabyte.kotter.foundation.liveVarOf
import com.varabyte.kotter.foundation.runUntilSignal
import com.varabyte.kotter.foundation.text.*
import com.varabyte.kotter.runtime.Session
import kotlin.math.max
import kotlin.math.min

fun Session.fromPicker(
    currency: CurrenciesResponse,
    linesPerPage: Int,
    totalPages: Int
): Pair<String, String> {
    val entries = currency.entries.toList()

    var cursorIndex by liveVarOf(0)
    var currentPage by liveVarOf(0)

    section {
        bold { textLine("Select a currency to convert from:") }
        textLine()

        val startIndex = currentPage * linesPerPage
        val endIndex = min((currentPage + 1) * linesPerPage, entries.size)

        for (index in startIndex until endIndex) {
            val (code, name) = entries[index]
            text(if (index == cursorIndex) "▶️ " else "   ")
            textLine("$code ($name)")
        }

        textLine()
        black(isBright = true) {
            textLine("Page ${currentPage + 1} of $totalPages")
            textLine("Use the arrow keys to navigate, Page Up/Down to scroll, and ENTER to select.")
        }

        if (cursorIndex < startIndex) {
            cursorIndex = startIndex
        } else if (cursorIndex >= endIndex) {
            cursorIndex = endIndex - 1
        }
    }.runUntilSignal {
        onKeyPressed {
            when (key) {
                Keys.UP -> {
                    cursorIndex -= 1

                    if (cursorIndex < currentPage * linesPerPage && currentPage > 0) {
                        currentPage--
                    }
                    rerender()
                }
                Keys.DOWN -> {
                    cursorIndex += 1

                    if (cursorIndex >= (currentPage + 1) * linesPerPage) currentPage++
                    rerender()
                }
                Keys.LEFT -> {
                    currentPage = max(currentPage - 1, 0)
                    cursorIndex = max(cursorIndex - linesPerPage, 0)
                    rerender()
                }
                Keys.RIGHT -> {
                    currentPage = min(currentPage + 1, totalPages - 1)
                    cursorIndex = min(cursorIndex + linesPerPage, entries.size - 1)
                    rerender()
                }
                Keys.ENTER -> signal()
                else -> { }
            }

            if (cursorIndex < 0) cursorIndex = entries.size - 1
            else if (cursorIndex >= entries.size) cursorIndex = 0
        }
    }

    val (code, name) = entries[cursorIndex]
    return code to name
}

fun Session.toPicker(
    currency: CurrenciesResponse,
    linesPerPage: Int,
    totalPages: Int
): Set<String> {
    val entries = currency.entries.toList()
    val confirmIndex = entries.size

    var cursorIndex by liveVarOf(0)
    var currentPage by liveVarOf(0)
    val selectedCurrencies = liveSetOf<String>()

    section {
        bold { textLine("Select currencies to convert from:") }
        textLine()

        val startIndex = currentPage * linesPerPage
        val endIndex = min((currentPage + 1) * linesPerPage, entries.size)

        for (index in startIndex until endIndex) {
            val (code, name) = entries[index]
            text(if (index == cursorIndex) "▶️ " else "   ")

            text('[')
            text(if (code in selectedCurrencies) "✔️" else "  ")
            text(']')
            text(" $code ($name)")
            textLine()
        }

        textLine()
        if (confirmIndex == cursorIndex) {
            textLine("▶️ Confirm")
        } else {
            textLine("   Confirm")
        }

        black(isBright = true) {
            textLine("Page ${currentPage + 1} of $totalPages")
            textLine()
            textLine("Use the arrow keys to navigate, SPACE to select, and ENTER to confirm.")
            textLine("Press HOME to jump to the top of the list and END to jump to the bottom.")
            textLine()
            textLine(
                "Selected currencies: ${
                    if (selectedCurrencies.isEmpty()) "None"
                    else selectedCurrencies.joinToString(", ")
                }"
            )
        }

        if (cursorIndex < startIndex) {
            cursorIndex = startIndex
        } else if (cursorIndex in endIndex..<confirmIndex) {
            cursorIndex = endIndex - 1
        }
    }.runUntilSignal {
        onKeyPressed {
            when (key) {
                Keys.UP -> {
                    cursorIndex--
                    if (cursorIndex < 0) {
                        cursorIndex = confirmIndex
                    }
                    if (cursorIndex < currentPage * linesPerPage && currentPage > 0) {
                        currentPage--
                    }
                    rerender()
                }
                Keys.DOWN -> {
                    cursorIndex++
                    if (cursorIndex > confirmIndex) {
                        cursorIndex = 0
                        currentPage = 0
                    } else if (cursorIndex >= (currentPage + 1) * linesPerPage && cursorIndex < confirmIndex) {
                        currentPage++
                    }
                    rerender()
                }
                Keys.LEFT -> {
                    currentPage = max(currentPage - 1, 0)
                    cursorIndex = max(cursorIndex - linesPerPage, 0)
                    rerender()
                }
                Keys.RIGHT -> {
                    currentPage = min(currentPage + 1, totalPages - 1)
                    cursorIndex = min(cursorIndex + linesPerPage, confirmIndex)
                    rerender()
                }
                Keys.SPACE -> {
                    if (cursorIndex < entries.size) {
                        val (code, _) = entries[cursorIndex]
                        if (code in selectedCurrencies) {
                            selectedCurrencies.remove(code)
                        } else {
                            selectedCurrencies.add(code)
                        }
                    }
                    rerender()
                }
                Keys.END -> {
                    cursorIndex = confirmIndex
                    rerender()
                }
                Keys.HOME -> {
                    cursorIndex = 0
                    rerender()
                }
                Keys.ENTER -> {
                    if (cursorIndex == confirmIndex) {
                        signal()
                    }
                }
                else -> {  }
            }

            if (cursorIndex < 0) cursorIndex = confirmIndex
            else if (cursorIndex > confirmIndex) cursorIndex = 0
        }
    }

    return selectedCurrencies
}