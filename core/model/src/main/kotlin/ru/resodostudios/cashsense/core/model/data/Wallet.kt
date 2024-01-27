package ru.resodostudios.cashsense.core.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Wallet(
    val id: String,
    val title: String,
    val startBalance: Double,
    val currency: Currency
) : Parcelable