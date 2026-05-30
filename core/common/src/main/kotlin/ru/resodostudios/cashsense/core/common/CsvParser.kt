package ru.resodostudios.cashsense.core.common

import javax.inject.Inject

class CsvParser @Inject constructor() {

    fun parse(line: String, separator: String): List<String> {
        if (separator.isEmpty()) return listOf(line)

        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false

        var i = 0
        while (i < line.length) {
            val char = line[i]
            when {
                char == '\"' -> inQuotes = !inQuotes
                line.startsWith(separator, i) && !inQuotes -> {
                    result.add(current.toString())
                    current = StringBuilder()
                    i += separator.length - 1
                }
                else -> current.append(char)
            }
            i++
        }
        result.add(current.toString())

        return result.map { it.trim().removeSurrounding("\"") }
    }
}
