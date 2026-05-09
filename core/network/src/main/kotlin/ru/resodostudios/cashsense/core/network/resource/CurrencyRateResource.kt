package ru.resodostudios.cashsense.core.network.resource

import io.ktor.resources.Resource

@Resource("rates/{base}/{target}/latest")
internal class CurrencyRateResource(
    val base: String,
    val target: String,
)