package ru.resodostudios.cashsense.core.model.data

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val title: String,
    val iconId: Int,
)