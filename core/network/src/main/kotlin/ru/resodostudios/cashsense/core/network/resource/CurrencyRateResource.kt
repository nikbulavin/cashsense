package ru.resodostudios.cashsense.core.network.resource

import io.ktor.resources.Resource

@Resource("{base}")
internal class CurrencyRateResource(
    val base: String,
    val target: String,
)