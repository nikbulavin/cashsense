package ru.resodostudios.cashsense.work.initializer

import androidx.work.Constraints
import androidx.work.NetworkType

val SyncConstraints
    get() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()