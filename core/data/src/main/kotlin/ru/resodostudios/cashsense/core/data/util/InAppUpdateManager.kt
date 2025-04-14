package ru.resodostudios.cashsense.core.data.util

import kotlinx.coroutines.flow.Flow

interface InAppUpdateManager {

    val inAppUpdateResult: Flow<InAppUpdateResult>
}