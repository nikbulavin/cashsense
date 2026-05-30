package ru.resodostudios.cashsense.core.model.data

data class CsvConfig(
    val columnSeparator: String = ";",
    val dateColumnIndex: Int = 0,
    val amountColumnIndex: Int = 1,
    val descriptionColumnIndex: Int = 2,
    val categoryColumnIndex: Int = 3,
    val ignoreFirstLine: Boolean = true,
    val dateFormat: String = "dd.MM.yyyy HH:mm:ss",
)
